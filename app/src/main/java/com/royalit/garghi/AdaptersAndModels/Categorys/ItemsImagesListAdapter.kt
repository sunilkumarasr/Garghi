package com.royalit.garghi.AdaptersAndModels.Categorys

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royalit.garghi.AdaptersAndModels.ImageDataList
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient

class ItemsImagesListAdapter(
    private val items: List<ImageDataList>,
    private val onItemClick: (ImageDataList) -> Unit // Click listener function
) : RecyclerView.Adapter<ItemsImagesListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgLogo: ImageView = itemView.findViewById(R.id.imgLogo)

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
            .inflate(R.layout.post_gallery_images_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.imgLogo).load(RetrofitClient.Image_Path+item.additionalImage.toString()).into(holder.imgLogo)

        Log.e("im__g",item.additionalImage.toString())

    }

    override fun getItemCount(): Int {
        return items.size
    }
}