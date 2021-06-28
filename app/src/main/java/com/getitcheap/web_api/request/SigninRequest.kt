package com.getitcheap.web_api.request

import com.google.gson.annotations.SerializedName

data class SigninRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)