package com.getitcheap.web_api.request

import com.google.gson.annotations.SerializedName

data class DeleteItemsRequest(
    @SerializedName("ids") val ids: List<Long>
)