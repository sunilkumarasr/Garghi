package com.royalit.garghi.AdaptersAndModels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royalit.garghi.R

class TestimonialsAdapter(
    private val items: List<TestimonialModel>,
    private val onItemClick: (TestimonialModel) -> Unit
) : RecyclerView.Adapter<TestimonialsAdapter.TestimonialViewHolder>() {

    class TestimonialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgTestimonial)
        val name: TextView = view.findViewById(R.id.txtTestimonialName)
        val message: TextView = view.findViewById(R.id.txtTestimonialMessage)
        val rating: RatingBar = view.findViewById(R.id.ratingTestimonial)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TestimonialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_testimonial, parent, false)

        return TestimonialViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TestimonialViewHolder,
        position: Int
    ) {
        val item = items[position]

        holder.name.text = item.name.orEmpty()
        holder.message.text = item.message.orEmpty()
            .trim()
            .trim('"')
            .trim()

        holder.rating.rating = (item.star?.toFloatOrNull() ?: 0f)
            .coerceIn(0f, 5f)

        Glide.with(holder.image)
            .load(item.image)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .circleCrop()
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}