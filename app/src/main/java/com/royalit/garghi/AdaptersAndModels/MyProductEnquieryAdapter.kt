package com.royalit.garghi.AdaptersAndModels

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royalit.garghi.R

class MyProductEnquieryAdapter(
    private val items: List<EnquieryProductModel>,
    private val onItemClick: (EnquieryProductModel) -> Unit // Click listener function
) : RecyclerView.Adapter<MyProductEnquieryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtEmail: TextView = itemView.findViewById(R.id.txtEmail)
        val txtPhone: TextView = itemView.findViewById(R.id.txtPhone)
        val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
        val imgCall: ImageView = itemView.findViewById(R.id.imgCall)

        init {
            // Set item click listener
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }

            // Set call button click listener
            imgCall.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${item.phone}")
                    }
                    // Use the context from itemView
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_post_enquiry_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtName.text = "Name: ${item.name}"
        holder.txtEmail.text = "Email: ${item.email}"
        holder.txtPhone.text = "Mobile Number: ${item.phone}"
        holder.txtMessage.text = "Message: ${item.message}"
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
