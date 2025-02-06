package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class AddPostResponse(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("message" ) var message : String? = null
)
