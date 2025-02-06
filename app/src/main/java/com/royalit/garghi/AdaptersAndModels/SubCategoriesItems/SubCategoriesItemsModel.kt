package com.royalit.garghi.AdaptersAndModels.SubCategoriesItems

data class SubCategoriesItemsModel(
    val id: String,
    val title: String,
    val slug: String,
    val categoryId: String,
    val address: String,
    val mail: String,
    val mobile: String,
    val location: String,
    val certified: Boolean,
    val verified: Boolean,
    val about: String,
    val services: String,
    val state: String,
    val city: String,
    val latitude: String?,
    val longitude: String?,
    val image: String,
    val createdDate: String,
    val createdTime: String
)
