package com.royalit.garghi.AdaptersAndModels

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.royalit.garghi.R

class ImageAdapter(
    private var imageUris: MutableList<Uri>,
    private val onRemoveClick: (Uri) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageUris[position]
        holder.bind(imageUri)
        holder.removeButton.setOnClickListener {
            onRemoveClick(imageUri)
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val removeButton: ImageButton = itemView.findViewById(R.id.btnRemove)

        fun bind(imageUri: Uri) {
            imageView.setImageURI(imageUri)  // Display image
        }
    }

    fun updateImages(newImageUris: MutableList<Uri>) {
        imageUris = newImageUris
        notifyDataSetChanged()
    }
}
