package com.royalit.garghi.AdaptersAndModels

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royalit.garghi.R
import java.util.Locale

class MySubscriptionAdapter :
    RecyclerView.Adapter<MySubscriptionAdapter.PlanViewHolder>() {

    private val items = mutableListOf<MySubscription>()

    fun submitList(newItems: List<MySubscription>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_subscription, parent, false)

        return PlanViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: PlanViewHolder,
        position: Int
    ) {
        val item = items[position]

        holder.txtPackage.text =
            item.title?.takeIf { it.isNotBlank() } ?: "Subscription"

        holder.txtPrice.text =
            item.price_display?.takeIf { it.isNotBlank() }
                ?: item.price?.let {
                    if (it.startsWith("₹")) it else "₹$it"
                }
                        ?: "—"

        val duration = item.duration
        val type = item.duration_type.orEmpty()
        holder.txtDuration.text =
            if (duration != null && type.isNotBlank()) {
                "$duration $type" +
                        if (duration != 1 && !type.endsWith("s")) "s" else ""
            } else {
                "Duration unavailable"
            }

        val count = item.listing_count
        holder.txtListingsIncluded.text = when (count) {
            null -> "Listings included: —"
            1 -> "✓  1 listing included"
            else -> "✓  $count listings included"
        }

        holder.txtStart.text =
            item.start_date?.takeIf { it.isNotBlank() } ?: "—"

        holder.txtEnd.text =
            item.end_date?.takeIf { it.isNotBlank() } ?: "—"

        holder.txtPaymentStatus.text =
            "Payment: ${item.payment_status ?: "—"}"

        val status = item.status_label
            ?.takeIf { it.isNotBlank() }
            ?: "Unknown"

        holder.txtStatus.text = status.uppercase(Locale.ROOT)

        when (status.lowercase(Locale.ROOT)) {
            "active" -> {
                holder.txtStatus.setBackgroundResource(
                    R.drawable.bg_status_active
                )
                holder.txtStatus.setTextColor(
                    Color.parseColor("#18743B")
                )
            }

            "pending verification" -> {
                holder.txtStatus.setBackgroundResource(
                    R.drawable.bg_status_pending
                )
                holder.txtStatus.setTextColor(
                    Color.parseColor("#9B4D0B")
                )
            }

            else -> {
                holder.txtStatus.setBackgroundResource(
                    R.drawable.bg_status_failed
                )
                holder.txtStatus.setTextColor(
                    Color.parseColor("#B3262D")
                )
            }
        }
    }

    class PlanViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val txtPackage: TextView =
            view.findViewById(R.id.txtPackage)

        val txtStatus: TextView =
            view.findViewById(R.id.txtStatus)

        val txtPrice: TextView =
            view.findViewById(R.id.txtPrice)

        val txtDuration: TextView =
            view.findViewById(R.id.txtDuration)

        val txtListingsIncluded: TextView =
            view.findViewById(R.id.txtListingsIncluded)

        val txtStart: TextView =
            view.findViewById(R.id.txtStart)

        val txtEnd: TextView =
            view.findViewById(R.id.txtEnd)

        val txtPaymentStatus: TextView =
            view.findViewById(R.id.txtPaymentStatus)
    }
}