package com.getitcheap.web_api.response

import com.google.gson.annotations.SerializedName

data class SigninResponse (
    @SerializedName("jwt") val jwt:String
)