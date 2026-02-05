package com.royalit.garghi.AdaptersAndModels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royalit.garghi.R

class AskListAdapter(
    private val items: List<AskListModel>,
    private val name: String,
    private val onItemClick: (AskListModel) -> Unit // Click listener function
) : RecyclerView.Adapter<AskListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtQs: TextView = itemView.findViewById(R.id.txtQs)
        val txtAns: TextView = itemView.findViewById(R.id.txtAns)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ask_questions_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtName.text = name
        holder.txtDate.text = item.created_at
        holder.txtQs.text = item.title
        holder.txtAns.text = item.description
        holder.txtStatus.text = item.ticket_status
    }

    override fun getItemCount(): Int {
        return items.size
    }
}