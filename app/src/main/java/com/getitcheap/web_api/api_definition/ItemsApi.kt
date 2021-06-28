package com.getitcheap.web_api.api_definition

import com.getitcheap.web_api.request.NewItemRequest
import com.getitcheap.web_api.response.ItemsResponse
import com.getitcheap.web_api.response.MessageResponse
import retrofit2.Call
import retrofit2.http.*

interface ItemsApi {

    @GET("/items")
    fun getAllItems() : Call<List<ItemsResponse>>

    @POST("/item")
    fun newItem(@Header("Authorization") token: String, @Body request: NewItemRequest) : Call<MessageResponse>

}