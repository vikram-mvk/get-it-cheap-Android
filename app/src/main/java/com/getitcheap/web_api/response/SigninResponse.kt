package com.getitcheap.web_api.response

import com.google.gson.annotations.SerializedName

data class SigninResponse (
    @SerializedName("message") val message:String,
    @SerializedName("jwt") val jwt:String,
    @SerializedName("firstName") val firstName:String,
    @SerializedName("lastName") val lastName:String,
    @SerializedName("username") val username:String,
    @SerializedName("email") val email:String,
    @SerializedName("userId") val userId:Long

)