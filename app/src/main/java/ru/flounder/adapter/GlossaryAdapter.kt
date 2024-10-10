package ru.flounder.adapter

import Gsmf
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.flounder.R
import android.util.Base64
import android.media.MediaPlayer
import java.io.ByteArrayInputStream

class GlossaryAdapter(
    private val gsmf: Gsmf,
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<GlossaryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordTextView: TextView = view.findViewById(R.id.word_text_view)
        val definitionTextView: TextView = view.findViewById(R.id.definition_text_view)
        val exampleTextView: TextView = view.findViewById(R.id.example_text_view)
        val listenButton: Button = view.findViewById(R.id.listen_button)
        val translations: TextView = view.findViewById(R.id.translation_text_view)
        init {
            listenButton.setOnClickListener {
                itemClickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gloassary, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.wordTextView.text = gsmf.words[position]
        holder.definitionTextView.text = "\n\nDefenitions: \n ${gsmf.definitions[position].joinToString("\n-")}"
        holder.exampleTextView.text = "\n\nUsage Examples: \n${gsmf.examples[position].joinToString("\n-")}"
        holder.translations.text = "\n\nTranslation: ${gsmf.translations.get("ru")?.get(position)}\n"
    }

    override fun getItemCount(): Int = gsmf.words.size
}
