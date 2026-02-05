package com.royalit.garghi.AdaptersAndModels.Notifications

data class NotificationModel(
    val id: String,
    val title: String,
    val body: String,
    val created_date: String,
    val created_time: String,
    val product_id: String,
    val type: String,
    var product_name: String=""
)