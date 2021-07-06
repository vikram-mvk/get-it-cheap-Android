package com.getitcheap.web_api.response

import com.google.gson.annotations.SerializedName

data class SigninResponse (
    @SerializedName("jwt") val jwt:String,
    @SerializedName("username") val username:String,
    @SerializedName("email") val email:String,
    @SerializedName("userId") val userId:Long

)