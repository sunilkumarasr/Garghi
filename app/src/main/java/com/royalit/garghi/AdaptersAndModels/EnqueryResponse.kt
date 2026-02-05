package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class EnqueryResponse(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("message" ) var message : String? = null
)

data class EnqueryRequest(
    val name: String,
    val phone: String,
    val email: String,
    val message: String,
    val product_id: String,
    val category_id: String,
    val subcategory_id: String,
    val created_by: String
)

data class EnquerySaleRequest(
    val name: String,
    val phone: String,
    val email: String,
    val message: String,
    val product: String,
    val created_by: String
)