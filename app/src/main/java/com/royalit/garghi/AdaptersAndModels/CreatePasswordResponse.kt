package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class CreatePasswordResponse(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("message" ) var message : String? = null
)
data class CreatePasswordRequest(
    val email: String,
    val new_password: String,
    val confirm_password: String
)
