package com.getitcheap.web_api.response

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("message") val message:String
)