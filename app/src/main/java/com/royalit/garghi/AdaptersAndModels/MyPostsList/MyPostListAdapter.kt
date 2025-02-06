package com.royalit.garghi.AdaptersAndModels.MyPostsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royalit.garghi.R

class MyPostListAdapter(
    private val items: List<MyPostsModel>,
    private val onItemClick: (MyPostsModel, Any?) -> Unit,// Click listener function
) : RecyclerView.Adapter<MyPostListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPostDate: TextView = itemView.findViewById(R.id.txtPostDate)
        val imgLogo: ImageView = itemView.findViewById(R.id.imgLogo)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val txtActiveStatus: TextView = itemView.findViewById(R.id.txtActiveStatus)
        val linearEnqury: LinearLayout = itemView.findViewById(R.id.linearEnqury)
        val linearView: LinearLayout = itemView.findViewById(R.id.linearView)
        val linearEdit: LinearLayout = itemView.findViewById(R.id.linearEdit)
        val linearDelete: LinearLayout = itemView.findViewById(R.id.linearDelete)


        init {

            linearEnqury.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position],"Enquiry")
                }
            }

            linearView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position],"View")
                }
            }

            linearEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position],"Edit")
                }
            }

            linearDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position],"Delete")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_post_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        if (item.status.equals("1")){
            holder.txtActiveStatus.text = "Status: Active"
            holder.txtActiveStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        }else{
            holder.txtActiveStatus.text = "Status: Pending"
            holder.txtActiveStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.selectedRed))
        }

        holder.txtTitle.text = item.title
        holder.txtPostDate.text = "Posted On "+item.created_date
        Glide.with(holder.imgLogo).load(item.image).error(R.drawable.vision_dummy).into(holder.imgLogo)

    }

    override fun getItemCount(): Int {
        return items.size
    }
}