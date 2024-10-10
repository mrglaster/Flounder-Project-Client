package ru.flounder.fragments.menu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import ru.flounder.R
import ru.flounder.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.flounder.dto.StudyModuleRequest

class CreateModuleFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etTopic: EditText
    private lateinit var spLanguage: Spinner
    private lateinit var spTranslationLanguage: Spinner
    private lateinit var wordTranslationContainer: LinearLayout
    private lateinit var btnAddWordTranslation: Button
    private lateinit var btnCreateModule: Button

    private val wordPairs = mutableListOf<Pair<String, String>>()  // список пар слов и переводов

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_module, container, false)
        etTopic = view.findViewById(R.id.et_topic)
        spLanguage = view.findViewById(R.id.sp_language)
        spTranslationLanguage = view.findViewById(R.id.sp_translation_language)
        wordTranslationContainer = view.findViewById(R.id.word_translation_container)
        btnAddWordTranslation = view.findViewById(R.id.btn_add_word_translation)
        btnCreateModule = view.findViewById(R.id.btn_create_module)
        setupSpinners()
        btnAddWordTranslation.setOnClickListener {
            addWordTranslationField()
        }
        btnCreateModule.setOnClickListener {
            createStudyModule()
        }
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return view
    }

    private fun setupSpinners() {
        val languages = arrayOf("en", "ru", "de")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLanguage.adapter = adapter
        spTranslationLanguage.adapter = adapter
    }

    private fun addWordTranslationField() {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val etWord = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Word"
        }

        val etTranslation = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Translation"
        }

        row.addView(etWord)
        row.addView(etTranslation)
        wordTranslationContainer.addView(row)
    }

    private fun createStudyModule() {
        // Получение данных с UI
        val topic = etTopic.text.toString()
        val language = spLanguage.selectedItem.toString()
        val translationLanguage = spTranslationLanguage.selectedItem.toString()

        for (i in 0 until wordTranslationContainer.childCount) {
            val row = wordTranslationContainer.getChildAt(i) as LinearLayout
            val etWord = row.getChildAt(0) as EditText
            val etTranslation = row.getChildAt(1) as EditText
            val word = etWord.text.toString()
            val translation = etTranslation.text.toString()
            wordPairs.add(word to translation)
        }

        val words = wordPairs.map { it.first }
        val translations = wordPairs.map { it.second }

        // Получение userId и токена
        val userIdStr = sharedPreferences.getString("user_id", "0")
        val userId = userIdStr?.let { Integer.parseInt(it) } ?: 0
        val token = sharedPreferences.getString("token", "") ?: ""
        Log.d("BODY IS", "${userId} ${topic} ${language} ${words} ${translations} ${translationLanguage} ${"default"}")
        val requestBody = StudyModuleRequest(
            author_id = userId,
            topic = topic,
            language = language,
            words = words,
            translations = translations,
            translations_language = translationLanguage,
            icon = "default"
        )
        RetrofitClient.apiService.createStudyModule("Bearer $token", requestBody)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Module created successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}