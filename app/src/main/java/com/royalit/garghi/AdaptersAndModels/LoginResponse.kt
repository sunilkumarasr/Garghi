package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("user") val user: User? = null
)

data class User(
    @SerializedName("id"  ) var id  : String?    = null,
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("location" ) var location : String? = null,
    @SerializedName("email" ) var email : String? = null,
    @SerializedName("phone" ) var phone : String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)
