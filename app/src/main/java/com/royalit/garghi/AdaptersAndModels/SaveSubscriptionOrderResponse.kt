package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class SaveSubscriptionOrderResponse(
    @SerializedName("status")
    var status: String? = null,

    @SerializedName("message")
    var message: String? = null,
)


data class SaveSubscriptionOrderRequest(
    val user_id: String,
    val user_subscription_id: String,
    val razorpay_payment_id: String,
)