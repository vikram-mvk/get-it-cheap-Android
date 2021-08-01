package com.getitcheap.web_api.request

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest (
    @SerializedName("id") var id: Long,
    @SerializedName("firstName") var firstName: String?,
    @SerializedName("lastName") var lastName: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("password") var password: String?
    )