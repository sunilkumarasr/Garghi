package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("status"  ) var status  : String?    = null,
    @SerializedName("data") val data: Data? = null
)

data class Data(
    @SerializedName("id"  ) var id  : String?    = null,
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("country_code" ) var country_code : String? = null,
    @SerializedName("phone" ) var phone : String? = null,
    @SerializedName("location" ) var location : String? = null,
    @SerializedName("latitude" ) var latitude : String? = null,
    @SerializedName("longitude" ) var longitude : String? = null,
    @SerializedName("km" ) var km : String? = null,
    @SerializedName("state" ) var state : String? = null,
    @SerializedName("city" ) var city : String? = null,
    @SerializedName("image" ) var image : String? = null,
    @SerializedName("email" ) var email : String? = null,
    @SerializedName("password" ) var password : String? = null,
    @SerializedName("created_at" ) var created_at : String? = null,
    @SerializedName("status" ) var status : String? = null,

)