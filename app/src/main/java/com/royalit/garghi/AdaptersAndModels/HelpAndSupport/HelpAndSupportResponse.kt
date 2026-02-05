package com.royalit.garghi.AdaptersAndModels.HelpAndSupport

import com.google.gson.annotations.SerializedName

data class HelpAndSupportResponse(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("message" ) var message : String? = null
)
data class HelpAndSupportRequest(
    val name: String,
    val email: String,
    val phone: String,
    val message: String
)
