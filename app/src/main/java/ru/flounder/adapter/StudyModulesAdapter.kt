package ru.flounder.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.flounder.R
import ru.flounder.dto.StudyModuleInfoResponseDTO

class StudyModulesAdapter(
    private var studyModules: List<StudyModuleInfoResponseDTO>,
    private val onDownloadClicked: (String, Button) -> Unit
) : RecyclerView.Adapter<StudyModulesAdapter.StudyModuleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyModuleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_study_module, parent, false)
        return StudyModuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudyModuleViewHolder, position: Int) {
        val module = studyModules[position]
        holder.bind(module)
    }

    override fun getItemCount(): Int = studyModules.size

    inner class StudyModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.icon_image_view)
        private val topicTextView: TextView = itemView.findViewById(R.id.topic_text_view)
        private val languageImageView: ImageView = itemView.findViewById(R.id.language_image_view)
        private val wordsTextView: TextView = itemView.findViewById(R.id.words_text_view)
        private val downloadButton: Button = itemView.findViewById(R.id.download_button)

        fun bind(module: StudyModuleInfoResponseDTO) {
            topicTextView.text = module.topic
            wordsTextView.text = module.displayWords
            val moduleName = module.filePath.substringAfterLast("/")
            val isDownloaded = checkIfDownloaded(moduleName)
            if (isDownloaded) {
                downloadButton.isEnabled = false
                downloadButton.text = "Downloaded"
            } else {
                downloadButton.isEnabled = true
                downloadButton.text = "Download"
                downloadButton.setOnClickListener {
                    onDownloadClicked(moduleName, downloadButton)
                }
            }
            val languageIconResId = if (module.language == "de") R.drawable.de else R.drawable.uk
            languageImageView.setImageResource(languageIconResId)
            Log.d("CONTENT LOADING", "http://192.168.0.121:8080/download/image/${module.iconPath.substringAfterLast("/")}")
            Glide.with(itemView.context)
                .load("http://192.168.0.121:8080/download/image/${module.iconPath.substringAfterLast("/")}")
                .into(iconImageView)

        }
        private fun checkIfDownloaded(moduleName: String): Boolean {
            val sharedPreferences = itemView.context.getSharedPreferences("downloads", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(moduleName, false)
        }
    }
}
