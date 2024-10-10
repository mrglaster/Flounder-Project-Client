package ru.flounder.fragments.menu
import Gsmf
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.flounder.R

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.flounder.adapter.DownloadedModulesAdapter
import java.io.File
import android.widget.Toast
import parseGsmf
import ru.flounder.fragments.study.SelectActionsFragment

class DownloadsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DownloadedModulesAdapter
    private val gsmfList = mutableListOf<Gsmf>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_downloads, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = DownloadedModulesAdapter(gsmfList) { gsmf ->
            val selectActionsFragment = SelectActionsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("gsmf", gsmf)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectActionsFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter
        loadDownloadedModules()

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadDownloadedModules() {
        val storageDir = File(context?.filesDir.toString())
        if (!storageDir.exists() || !storageDir.isDirectory) {
            Toast.makeText(activity, "Download directory does not exist.", Toast.LENGTH_SHORT)
                .show()
            return
        }
        storageDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension == "gsmf") {
                try {
                    val moduleContent = file.readText()
                    val gsmf = parseGsmf(moduleContent)
                    Log.d("TEST", gsmf.author)
                    gsmf?.let { gsmfList.add(it) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        activity,
                        "Failed to read file: ${file.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}
