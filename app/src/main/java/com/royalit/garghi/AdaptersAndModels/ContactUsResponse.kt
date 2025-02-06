package com.royalit.garghi.AdaptersAndModels

import com.google.gson.annotations.SerializedName

data class ContactUsResponse(
    @SerializedName("address"  ) var address  : String?    = null,
    @SerializedName("phone"  ) var phone  : String?    = null,
    @SerializedName("email"  ) var email  : String?    = null,
    @SerializedName("logo"  ) var logo  : String?    = null,
)
