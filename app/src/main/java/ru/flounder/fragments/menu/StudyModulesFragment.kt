package ru.flounder.fragments.menu
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.flounder.R
import ru.flounder.adapter.StudyModulesAdapter
import ru.flounder.dto.StudyModuleInfoResponseDTO
import ru.flounder.retrofit.RetrofitClient
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.net.URL

class StudyModulesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var studyModulesAdapter: StudyModulesAdapter
    private val studyModulesList = mutableListOf<StudyModuleInfoResponseDTO>()
    private lateinit var searchEditText: EditText
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_study_modules, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        searchEditText = view.findViewById(R.id.search_edit_text)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        studyModulesAdapter = StudyModulesAdapter(studyModulesList) { moduleName, downloadButton ->
            onDownloadClicked(moduleName, downloadButton)
        }
        recyclerView.adapter = studyModulesAdapter

        fetchStudyModules()
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchStudyModules(query)
                } else {
                    fetchStudyModules()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun fetchStudyModules() {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        if (token.isNullOrEmpty()) {
            Toast.makeText(activity, "Token is missing. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.apiService.getLatestStudyModules("Bearer $token").enqueue(object : Callback<List<StudyModuleInfoResponseDTO>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<StudyModuleInfoResponseDTO>>, response: Response<List<StudyModuleInfoResponseDTO>>) {
                if (response.isSuccessful) {
                    studyModulesList.clear()
                    studyModulesList.addAll(response.body() ?: emptyList())
                    studyModulesAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(activity, "Failed to load modules: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<StudyModuleInfoResponseDTO>>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchStudyModules(query: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        if (token.isNullOrEmpty()) {
            Toast.makeText(activity, "Token is missing. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.apiService.searchStudyModules("Bearer $token", mapOf("substring" to query)).enqueue(object : Callback<List<StudyModuleInfoResponseDTO>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<StudyModuleInfoResponseDTO>>, response: Response<List<StudyModuleInfoResponseDTO>>) {
                if (response.isSuccessful) {
                    studyModulesList.clear()
                    studyModulesList.addAll(response.body() ?: emptyList())
                    studyModulesAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(activity, "Failed to load modules: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<StudyModuleInfoResponseDTO>>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onDownloadClicked(moduleName: String, downloadButton: Button) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)

            Toast.makeText(activity, "Permission required to download files.", Toast.LENGTH_SHORT).show()

            return
        }

        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        if (token.isNullOrEmpty()) {
            Toast.makeText(activity, "Token is missing. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.apiService.downloadModule("Bearer $token", moduleName).enqueue(object : Callback<ResponseBody> {
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val gsmfContent = responseBody.string()
                        saveFile(gsmfContent, moduleName)
                        downloadButton.isEnabled = false
                        downloadButton.text = "Downloaded"
                    }
                } else {
                    Toast.makeText(activity, "Failed to download: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun saveFile(content: String, moduleName: String) {
        try {
            val path = context?.filesDir
            val file = File(path, moduleName)
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos).use { writer ->
                    writer.write(content)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Permission granted. You can now download files.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Permission denied. Cannot download files.", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
