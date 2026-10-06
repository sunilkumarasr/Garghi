package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class MySubscription(
    val id: Int? = null,
    val user_id: Int? = null,
    val subscription_id: Int? = null,
    val title: String? = null,
    val price: String? = null,
    val price_display: String? = null,
    val duration: Int? = null,
    val duration_type: String? = null,

    @SerializedName(
        value = "listings_included",
        alternate = ["listing_count"]
    )
    val listing_count: Int? = null,

    val total_listings_purchased: Int? = null,
    val total_listings_available: Int? = null,

    @SerializedName(
        value = "listings_used",
        alternate = ["total_listings_used"]
    )
    val listings_used: Int? = null,

    val start_date: String? = null,
    val end_date: String? = null,
    val start_date_raw: String? = null,
    val end_date_raw: String? = null,
    val payment_status: String? = null,
    val status: Int? = null,
    val status_label: String? = null,
    val is_active: Int? = null,
    val created_date: String? = null,
    val created_time: String? = null
) {
    // Computed property: Gson does not create another mapped field.
    val total_listings_used: Int?
        get() = listings_used
}