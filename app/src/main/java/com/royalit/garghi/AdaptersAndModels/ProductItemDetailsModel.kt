package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class ProductItemDetailsModel(
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") val data: DataProduct? = null
)


data class DataProduct(
    @SerializedName("product") val product: ProductDataProduct? = null,
    @SerializedName("images") var images: List<ImageDataProductList>? = null
)

data class ProductDataProduct(
    @SerializedName("id") var id: String? = null,
    @SerializedName("product") var product: String? = null,
    @SerializedName("actual_price") var actual_price: String? = null,
    @SerializedName("offer_price") var offer_price: String? = null,
    @SerializedName("color") var color: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("features") var features: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("created_by") var created_by: String? = null,
    @SerializedName("created_at") var created_at: String? = null,
    @SerializedName("updated_by") var updated_by: String? = null,
    @SerializedName("updated_at") var updated_at: String? = null,
    @SerializedName("status") var status: String? = null
)

data class ImageDataProductList(
    @SerializedName("id") var id: String? = null,
    @SerializedName("product_id") var productId: String? = null,
    @SerializedName("additional_image") var additionalImage: String? = null
)