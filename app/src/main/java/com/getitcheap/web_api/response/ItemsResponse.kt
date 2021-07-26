package com.getitcheap.web_api.response

import com.google.gson.annotations.SerializedName

data class ItemsResponse(
    @SerializedName("message") val message:String,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("itemType") val itemType: String,
    @SerializedName("image") val image: String,
    @SerializedName("price") val price: String,
    @SerializedName("rentalBasis") val rentalBasis: String?,
    @SerializedName("userId") val userId: Long,
    @SerializedName("username") val username: String,
    @SerializedName("itemLocation") val itemLocation: String,
    @SerializedName("contact") val contact: String,
    @SerializedName("datePosted") val datePosted: String

    )