package ru.flounder.adapter
import Gsmf
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.flounder.R
import java.io.ByteArrayInputStream

class DownloadedModulesAdapter(
    private val gsmfList: List<Gsmf>,
    private val clickListener: (Gsmf) -> Unit
) : RecyclerView.Adapter<DownloadedModulesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.topic_text_view)
        val authorTextView: TextView = view.findViewById(R.id.words_text_view)
        val iconImageView: ImageView = view.findViewById(R.id.icon_image_view)
        val languageImageView: ImageView = view.findViewById(R.id.language_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_downloaded_module, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gsmf = gsmfList[position]

        holder.titleTextView.text = gsmf.title ?: "Unknown Title"
        holder.authorTextView.text = gsmf.words.joinToString()

        if (gsmf.cover == "default"){
            holder.iconImageView.setImageResource(R.drawable.basic_cover)
        } else {
            val imageBytes = Base64.decode(gsmf.cover, Base64.DEFAULT)
            val inputStream = ByteArrayInputStream(imageBytes)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            holder.iconImageView.setImageBitmap(bitmap)
        }
        when (gsmf.wlang) {
            "en" -> holder.languageImageView.setImageResource(R.drawable.uk)
            "de" -> holder.languageImageView.setImageResource(R.drawable.de)
            else -> holder.languageImageView.setImageResource(R.drawable.uk)
        }
        holder.itemView.setOnClickListener {
            clickListener(gsmf)
        }
    }
    override fun getItemCount(): Int {
        return gsmfList.size
    }
}
