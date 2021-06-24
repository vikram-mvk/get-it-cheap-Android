package com.getitcheap.web_requests.items

import retrofit2.Call
import retrofit2.http.GET

interface ItemsApi {
    @GET("/items")
    fun getAllItems() : Call<List<ItemsModel>>

}