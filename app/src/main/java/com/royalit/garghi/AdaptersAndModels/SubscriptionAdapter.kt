package com.royalit.garghi.AdaptersAndModels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royalit.garghi.R

class SubscriptionAdapter(
    private val onBuyClick: (SubscriptionPlan) -> Unit
) : RecyclerView.Adapter<SubscriptionAdapter.PlanViewHolder>() {

    private val plans = mutableListOf<SubscriptionPlan>()

    fun submitList(newPlans: List<SubscriptionPlan>) {
        plans.clear()
        plans.addAll(newPlans)
        notifyDataSetChanged()
    }

    class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txtTitle)
        val price: TextView = view.findViewById(R.id.txtPrice)
        val duration: TextView = view.findViewById(R.id.txtDuration)
        val listings: TextView = view.findViewById(R.id.txtListings)
        val validity: TextView = view.findViewById(R.id.txtValidity)
        val description: TextView = view.findViewById(R.id.txtDescription)
        val buyNow: TextView = view.findViewById(R.id.btnBuyNow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subscription_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun getItemCount(): Int = plans.size

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]
        val unit = plan.duration_type.lowercase()
        val durationLabel = "${
            plan.duration
        } $unit${if (plan.duration != 1 && !unit.endsWith("s")) "s" else ""}"

        holder.title.text = plan.title
        holder.price.text = "${plan.price_display?.takeIf { it.isNotBlank() } ?: "₹${plan.price}"}/plan"
        holder.duration.text = "$durationLabel access"
        holder.listings.text = "✓  ${plan.listing_count} listing${if (plan.listing_count == 1) "" else "s"} included"
        holder.validity.text = "✓  Valid for $durationLabel"

        holder.description.apply {
            val details = plan.description.orEmpty().trim()
            visibility = if (details.isEmpty()) View.GONE else View.VISIBLE
            text = "✓  $details"
        }

        holder.buyNow.setOnClickListener { onBuyClick(plan) }
    }
}