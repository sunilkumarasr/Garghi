package com.royalit.garghi.AdaptersAndModels

data class SubscriptionPlan(
    val id: Int,
    val title: String,
    val duration: Int,
    val duration_type: String,
    val listing_count: Int,
    val price: String,
    val price_display: String?,
    val description: String?
)
