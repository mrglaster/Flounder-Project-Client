package ru.flounder.fragments.menu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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
import java.net.URL

class MyModulesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var studyModulesAdapter: StudyModulesAdapter
    private val studyModulesList = mutableListOf<StudyModuleInfoResponseDTO>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_modules, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        studyModulesAdapter = StudyModulesAdapter(studyModulesList) { moduleName, downloadButton ->
            onDownloadClicked(moduleName, downloadButton)
        }
        recyclerView.adapter = studyModulesAdapter

        fetchUserStudyModules()

        return view
    }

    private fun fetchUserStudyModules() {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        val userIdStr = sharedPreferences.getString("user_id", "0")
        var userId = userIdStr?.let { Integer.parseInt(it) }
        if (token.isNullOrEmpty() || userId == 0) {
            Toast.makeText(activity, "Token or user ID is missing. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = mapOf("user_id" to userId)
        RetrofitClient.apiService.getUserStudyModules("Bearer $token", requestBody).enqueue(object : Callback<List<StudyModuleInfoResponseDTO>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<StudyModuleInfoResponseDTO>>, response: Response<List<StudyModuleInfoResponseDTO>>) {
                if (response.isSuccessful) {
                    studyModulesList.clear()
                    studyModulesList.addAll(response.body() ?: emptyList())
                    studyModulesAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(activity, "Failed to load user modules: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<StudyModuleInfoResponseDTO>>, t: Throwable) {
                Toast.makeText(activity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onDownloadClicked(moduleName: String, downloadButton: Button) {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        if (token.isNullOrEmpty()) {
            Toast.makeText(activity, "Token is missing. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.apiService.downloadModule("Bearer $token", moduleName).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val filePath = responseBody.string() // Используйте метод для извлечения строки
                        saveFile(filePath, moduleName)
                        downloadButton.isEnabled = false
                        downloadButton.text = "Downloaded"

                        val prefs = requireActivity().getSharedPreferences("downloads", Context.MODE_PRIVATE)
                        prefs.edit().putBoolean(moduleName, true).apply()
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

    private fun saveFile(filePath: String, moduleName: String) {
        try {
            val storageDir = File(Environment.getExternalStorageDirectory(), "DownloadedModules")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            val file = File(storageDir, moduleName)
            val inputStream: InputStream = URL(filePath).openStream()
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}