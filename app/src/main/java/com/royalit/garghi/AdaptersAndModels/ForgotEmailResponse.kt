package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class ForgotEmailResponse(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("message" ) var message : String? = null
)



data class EmailRequest(
    val email: String
)
