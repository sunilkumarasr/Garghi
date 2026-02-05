package com.royalit.garghi.AdaptersAndModels

data class MyProductsModel(
    val id: String,
    val product: String,
    val actual_price: String,
    val offer_price: String,
    val address: String,
    val features: String,
    val description: String,
    val created_by: String,
    val updated_by: String,
    val updated_at: String,
    val status: String,
    val created_date: String,
    val created_time: String,
    val additional_images: List<String>
)