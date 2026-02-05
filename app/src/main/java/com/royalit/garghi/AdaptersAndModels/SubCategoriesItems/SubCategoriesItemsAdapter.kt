package com.royalit.garghi.AdaptersAndModels.SubCategoriesItems

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royalit.garghi.R

class SubCategoriesItemsAdapter(
    private var items: List<SubCategoriesItemsModel>,
    private val onItemClick: (SubCategoriesItemsModel, String) -> Unit // Click listener function
) : RecyclerView.Adapter<SubCategoriesItemsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtAbout: TextView = itemView.findViewById(R.id.txtAbout)
        val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)
        val txtMobile: TextView = itemView.findViewById(R.id.txtMobile)
        val txtEmail: TextView = itemView.findViewById(R.id.txtEmail)
        val imgCertified: ImageView = itemView.findViewById(R.id.imgCertified)
        val imgVertified: ImageView = itemView.findViewById(R.id.imgVertified)
        val imgLogo: ImageView = itemView.findViewById(R.id.imgLogo)
        val linearCall: LinearLayout = itemView.findViewById(R.id.linearCall)
        val linearViewMore: LinearLayout = itemView.findViewById(R.id.linearViewMore)

        init {
            linearCall.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position], "call")
                }
            }
            linearViewMore.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position], "view")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sub_categories_item_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtTitle.text = item.title
        holder.txtAbout.text = item.about
        holder.txtLocation.text = item.location
        holder.txtMobile.text = item.mobile
        holder.txtEmail.text = item.mail
        Glide.with(holder.imgLogo).load(item.image).into(holder.imgLogo)

        if (item.certified.equals("1")){
            holder.imgCertified.visibility = View.VISIBLE
        }

        if (item.verified.equals("1")){
            holder.imgVertified.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    //search
    fun updateList(newItems: List<SubCategoriesItemsModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
