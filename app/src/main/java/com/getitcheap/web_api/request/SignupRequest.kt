package com.getitcheap.web_api.request

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)