package com.royalit.garghi.AdaptersAndModels.Faq

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royalit.garghi.R

class FaqAdapter(
    private val items: List<FaqModel>,
    private val onItemClick: (FaqModel) -> Unit // Click listener function
) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expandCollapseButton: ImageView = itemView.findViewById(R.id.expandCollapseButton)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtDec: TextView = itemView.findViewById(R.id.txtDec)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    item.isExpanded = !item.isExpanded // Toggle expanded state
                    notifyItemChanged(position) // Notify that the item state has changed
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.faq_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtTitle.text = item.title
        //html text
        val htmlContent = item.description
        holder.txtDec.text = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)
        holder.txtDec.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

        holder.expandCollapseButton.setImageResource(
            if (item.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
        )

    }

    override fun getItemCount(): Int {
        return items.size
    }
}