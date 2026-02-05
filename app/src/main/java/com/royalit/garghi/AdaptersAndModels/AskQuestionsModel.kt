package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class AskQuestionsModel(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("message" ) var message : String? = null
)

data class AskQuestionsRequest(
    val title: String,
    val description: String,
    val created_by: String
)