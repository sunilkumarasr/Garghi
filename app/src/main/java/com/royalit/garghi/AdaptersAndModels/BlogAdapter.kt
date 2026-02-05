package com.royalit.garghi.AdaptersAndModels

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royalit.garghi.R

class BlogAdapter(
    private val items: List<BlogListModel>,
                  private val onItemClick: (BlogListModel) -> Unit // Click listener function
) : RecyclerView.Adapter<BlogAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.img)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtShortDec: TextView = itemView.findViewById(R.id.txtShortDec)
        val txtDec: TextView = itemView.findViewById(R.id.txtDec)

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
            .inflate(R.layout.blog_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtTitle.text = item.title
        holder.txtShortDec.text = item.short_description
        //html text
        val htmlContent = item.description
        holder.txtDec.text = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)

        Glide.with(holder.img).load(item.image.toString()).into(holder.img)

    }

    override fun getItemCount(): Int {
        return items.size
    }
}