package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class OTPResponse(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("user") val user: UserOtp? = null
)

data class UserOtp(
    @SerializedName("id"  ) var id  : String?    = null,
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("email" ) var email : String? = null,
    @SerializedName("phone" ) var phone : String? = null
)


data class OTPRequest(
    val email: String,
    val otp: String,
    val firebase_token: String
)