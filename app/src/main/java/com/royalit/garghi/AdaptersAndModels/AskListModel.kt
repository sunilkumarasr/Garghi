package com.royalit.garghi.AdaptersAndModels

data class AskListModel(
    val id: String,
    val title: String,
    val description: String,
    val created_at: String,
    val created_by: String,
    val updated_at: String,
    val updated_by: String,
    val status: String,
    val ticket_status: String,
)
