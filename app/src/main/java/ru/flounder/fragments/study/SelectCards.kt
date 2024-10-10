package ru.flounder.fragments.study

import Gsmf
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import ru.flounder.R

class SelectCards : Fragment() {

    private lateinit var wordTextView: TextView
    private lateinit var translationTextView: TextView
    private lateinit var backButton: Button
    private lateinit var nextButton: Button

    private lateinit var gsmf: Gsmf
    private var currentWordIndex: Int = 0

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
        val view = inflater.inflate(R.layout.fragment_select_cards, container, false)

        wordTextView = view.findViewById(R.id.word_text_view)
        translationTextView = view.findViewById(R.id.translation_text_view)
        backButton = view.findViewById(R.id.back_button)
        nextButton = view.findViewById(R.id.next_button)

        // Устанавливаем начальные данные
        updateUI()

        wordTextView.setOnClickListener {
            toggleTranslationVisibility()
        }

        backButton.setOnClickListener {
            if (currentWordIndex > 0) {
                currentWordIndex--
                updateUI()
            }
        }

        nextButton.setOnClickListener {
            if (currentWordIndex < gsmf.words.size - 1) {
                currentWordIndex++
                updateUI()
            }
        }

        return view
    }

    private fun updateUI() {
        val word = gsmf.words[currentWordIndex]
        wordTextView.text = word
        translationTextView.text = gsmf.translations.get("ru")?.get(currentWordIndex)
        translationTextView.visibility = View.GONE
    }

    private fun toggleTranslationVisibility() {
        if (translationTextView.visibility == View.GONE) {
            translationTextView.visibility = View.VISIBLE
        } else {
            translationTextView.visibility = View.GONE
        }
    }

}
