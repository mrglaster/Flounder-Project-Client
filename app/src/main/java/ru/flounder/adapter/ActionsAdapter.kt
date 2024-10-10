package ru.flounder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.flounder.R

class ActionsAdapter(private val clickListener: (String) -> Unit) : RecyclerView.Adapter<ActionsAdapter.ViewHolder>() {

    private val actions = listOf("Module Glossary", "Cards", "Training")

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val actionTextView: TextView = view.findViewById(R.id.action_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_action, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val action = actions[position]
        holder.actionTextView.text = action
        holder.itemView.setOnClickListener {
            clickListener(action)
        }
    }

    override fun getItemCount(): Int = actions.size
}
