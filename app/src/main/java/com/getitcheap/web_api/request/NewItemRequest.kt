package com.getitcheap.web_api.request

import com.google.gson.annotations.SerializedName

data class NewItemRequest(
    @SerializedName("itemName") val itemName: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("itemType") val itemType: String,
    @SerializedName("image") val image: String,
    @SerializedName("price") val price: Double,
    @SerializedName("rentalBasis") val rentalBasis: String?,
    @SerializedName("userId") val userId: Long,
    @SerializedName("username") val username: String,
    @SerializedName("contact") val contact: String
)