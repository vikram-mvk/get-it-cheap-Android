package com.getitcheap.web_api.api_definition

import com.getitcheap.web_api.response.ItemsResponse
import retrofit2.Call
import retrofit2.http.GET

interface ItemsApi {
    @GET("/items")
    fun getAllItems() : Call<List<ItemsResponse>>

}