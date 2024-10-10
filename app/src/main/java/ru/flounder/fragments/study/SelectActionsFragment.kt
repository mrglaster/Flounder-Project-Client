package ru.flounder.fragments.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.flounder.R
import Gsmf
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.flounder.adapter.ActionsAdapter
class SelectActionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActionsAdapter
    private lateinit var gsmf: Gsmf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gsmf = it.getSerializable("gsmf") as Gsmf
            Log.d("SelectActionsFragment", "Received gsmf: $gsmf")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_actions, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = ActionsAdapter { action ->
            when (action) {
                "Module Glossary" -> navigateToGlossary()  // Исправлено
                "Cards" -> navigateToCards()  // Исправлено
            }
        }
        recyclerView.adapter = adapter
        return view
    }

    private fun navigateToGlossary() {
        val glossaryFragment = ModuleGlossaryFragment().apply {
            arguments = Bundle().apply {
                putSerializable("gsmf", gsmf)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, glossaryFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToCards() {
        val cardsFragment = SelectCards().apply {
            arguments = Bundle().apply {
                putSerializable("gsmf", gsmf)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, cardsFragment)
            .addToBackStack(null)
            .commit()
    }
}

