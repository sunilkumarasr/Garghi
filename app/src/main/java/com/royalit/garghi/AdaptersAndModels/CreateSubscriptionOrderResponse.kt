package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class CreateSubscriptionOrderResponse(

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("payment_required")
    var paymentRequired: Int? = null,

    @SerializedName("data")
    var data: SubscriptionOrderData? = null
)

data class SubscriptionOrderData(

    @SerializedName("key_id")
    var keyId: String? = null,

    @SerializedName("order_id")
    var orderId: String? = null,

    @SerializedName("razorpay_order_id")
    var razorpayOrderId: String? = null,

    @SerializedName("amount")
    var amount: Int? = null,

    @SerializedName("currency")
    var currency: String? = null,

    @SerializedName("amount_display")
    var amountDisplay: String? = null,

    @SerializedName("price")
    var price: Int? = null,

    @SerializedName("user_subscription_id")
    var userSubscriptionId: String? = null,

    @SerializedName("subscription_id")
    var subscriptionId: Int? = null,

    @SerializedName("package_title")
    var packageTitle: String? = null,

    @SerializedName("user_id")
    var userId: Int? = null,

    @SerializedName("user_name")
    var userName: String? = null,

    @SerializedName("user_email")
    var userEmail: String? = null,

    @SerializedName("user_phone")
    var userPhone: String? = null,

    @SerializedName("payment_status")
    var paymentStatus: String? = null
)

data class CreateSubscriptionOrderRequest(
    val user_id: String,
    val subscription_id: String,
)