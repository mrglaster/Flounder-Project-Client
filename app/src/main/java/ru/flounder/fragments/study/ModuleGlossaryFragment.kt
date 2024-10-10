package ru.flounder.fragments.study

import Gsmf
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.flounder.R
import ru.flounder.adapter.GlossaryAdapter
import java.io.ByteArrayInputStream
import java.io.File

class ModuleGlossaryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GlossaryAdapter
    private lateinit var gsmf: Gsmf
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gsmf = it.getSerializable("gsmf") as Gsmf
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_module_glossary, container, false)

        recyclerView = view.findViewById(R.id.glossary_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = GlossaryAdapter(gsmf) { position ->
            playAudio(position)
        }

        recyclerView.adapter = adapter

        return view
    }

    private fun playAudio(position: Int) {
        if (gsmf.audios.isNotEmpty()) {
            val audioBase64 = gsmf.audios[position]
            val audioBytes = Base64.decode(audioBase64, Base64.DEFAULT)
            val inputStream = ByteArrayInputStream(audioBytes)

            val tempFile = File(requireContext().cacheDir, "audio_$position.mp3")
            tempFile.writeBytes(audioBytes)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
