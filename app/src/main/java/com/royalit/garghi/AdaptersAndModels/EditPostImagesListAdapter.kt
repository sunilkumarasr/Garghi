package com.royalit.garghi.AdaptersAndModels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient

class EditPostImagesListAdapter(
    private val items: MutableList<ImageDataList>,
    private val onItemClick: (ImageDataList) -> Unit // Click listener function
) : RecyclerView.Adapter<EditPostImagesListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDelete: ImageView = itemView.findViewById(R.id.imgDelete)
        val imgLogo: ImageView = itemView.findViewById(R.id.imgLogo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_post_gallery_images_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.imgLogo).load(RetrofitClient.Image_Path+item.additionalImage.toString()).into(holder.imgLogo)

//        holder.imgDelete.setOnClickListener {
//            items.removeAt(position)
//            // Notify the adapter that the item was removed
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, items.size)
//        }

        holder.imgDelete.setOnClickListener {
            onItemClick(items[position])
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

}