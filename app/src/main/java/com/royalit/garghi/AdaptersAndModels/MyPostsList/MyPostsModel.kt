package com.royalit.garghi.AdaptersAndModels.MyPostsList

data class MyPostsModel(
    val id: String,
    val title: String,
    val slug: String,
    val category_id: String,
    val subcategory: String?, // Nullable since it could be null
    val address: String,
    val mail: String,
    val mobile: String,
    val location: String,
    val certified: String,
    val verified: String,
    val about: String,
    val services: String,
    val state: String,
    val city: String,
    val latitude: String?,
    val longitude: String?,
    val status: String?,
    val image: String,
    val created_date: String,
    val created_time: String,
    val additional_images: List<String>
)
