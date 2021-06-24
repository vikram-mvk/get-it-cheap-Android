package com.getitcheap.web_requests.items

data class ItemsModel(
    val itemName: String,
    val description: String,
    val category: String,
    val itemType: String,
    val image: String,
    val price: String,
    val rentalBasis: String,
    val userId: Long,
    val username: String,
    val contact: String
)