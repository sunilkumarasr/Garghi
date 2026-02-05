package com.royalit.garghi.AdaptersAndModels.SalesHome

data class SaleModel(
    val status: String,
    val message: String,
    val data: List<ProductData>
)

data class ProductData(
    val product: SaleRes,
    val images: List<Images>
)

data class SaleRes(
    val id: String,
    val product: String,
    val actual_price: String,
    val offer_price: String,
    val address: String,
    val features: String,
    val description: String,
    val created_by: String,
    val created_at: String,
    val updated_by: String?,
    val updated_at: String,
    val status: String
)

data class Images(
    val id: String,
    val product_id: String,
    val additional_image: String
)