package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("message" ) var message : String? = null
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val country_code: String,
    val phone: String,
    val location: String,
    val km: String,
    val latitude: String,
    val longitude: String,
    val password: String
)