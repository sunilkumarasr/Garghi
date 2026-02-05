package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class PostItemDetailsModel(
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") val data: DataPost? = null
)

data class DataPost(
    @SerializedName("product") val product: ProductDataPost? = null,
    @SerializedName("images") var images: List<ImageDataList>? = null,
    @SerializedName("location_url") var location_url: String? = null,
    )

data class ProductDataPost(
    @SerializedName("id") var id: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("category_id") var categoryId: String? = null,
    @SerializedName("subcategory") var subcategory: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("mail") var mail: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("landline") var landline: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("certified") var certified: String? = null,
    @SerializedName("verified") var verified: String? = null,
    @SerializedName("about") var about: String? = null,
    @SerializedName("services") var services: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("latitude") var latitude: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("createdDate") var createdDate: String? = null,
    @SerializedName("createdTime") var createdTime: String? = null,
)

data class ImageDataList(
    @SerializedName("id") var id: String? = null,
    @SerializedName("product_id") var productId: String? = null,
    @SerializedName("additional_image") var additionalImage: String? = null
)