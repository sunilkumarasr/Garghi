package com.royalit.garghi.AdaptersAndModels.SalesHome

import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient

class SaleAdapter(
    private var items: List<ProductData>,
    private val onItemClick: (ProductData) -> Unit // Click listener function
) : RecyclerView.Adapter<SaleAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgLogo: ImageView = itemView.findViewById(R.id.imgLogo)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtOfferPrice: TextView = itemView.findViewById(R.id.txtOfferPrice)
        val txtActulePrice: TextView = itemView.findViewById(R.id.txtActulePrice)
        val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)

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
            .inflate(R.layout.sale_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        val firstImageUrl = item.images.firstOrNull()?.additional_image

        Glide.with(holder.imgLogo)
            .load(RetrofitClient.Image_Path + firstImageUrl)
            .placeholder(R.drawable.close_ic)
            .error(R.drawable.close_ic)
            .into(holder.imgLogo)

        holder.txtTitle.text = item.product.product
        holder.txtOfferPrice.text = "₹ " + item.product.offer_price
        holder.txtLocation.text = item.product.address

        val spannableString = SpannableString("₹ " + item.product.actual_price)
        spannableString.setSpan(StrikethroughSpan(), 0, spannableString.length, 0)
        holder.txtActulePrice.text = spannableString

    }

    override fun getItemCount(): Int {
        return items.size
    }

    //search
    fun updateList(newItems: List<ProductData>) {
        items = newItems
        notifyDataSetChanged()
    }

}