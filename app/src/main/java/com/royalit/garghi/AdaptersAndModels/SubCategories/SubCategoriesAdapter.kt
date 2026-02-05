package com.royalit.garghi.AdaptersAndModels.SubCategories

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royalit.garghi.R
import com.royalit.garghi.AdaptersAndModels.Categorys.SubCategoriesModel

class SubCategoriesAdapter(
    private val items: List<SubCategoriesModel>,
    private val onItemClick: (SubCategoriesModel) -> Unit // Click listener function
) : RecyclerView.Adapter<SubCategoriesAdapter.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linear: LinearLayout = itemView.findViewById(R.id.linear)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)  // Deselect previously selected item
                    selectedPosition = position  // Update selected position
                    notifyItemChanged(selectedPosition)  // Highlight the newly selected item
                    onItemClick(items[position])  // Handle click event
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sub_categories_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtTitle.text = item.subcategory

        // Change background or text color based on selected position
        if (position == selectedPosition) {
            holder.linear.setBackgroundResource(R.drawable.round_sub_cat)
            holder.txtTitle.setTextColor(Color.BLACK)
        } else {
            holder.linear.setBackgroundResource(R.drawable.round_edit_gray_full_edge)
            holder.txtTitle.setTextColor(Color.BLACK)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}
