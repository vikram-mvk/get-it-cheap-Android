package com.getitcheap.web_api.api_definition

import com.getitcheap.web_api.request.NewItemRequest
import com.getitcheap.web_api.response.ItemsResponse
import com.getitcheap.web_api.response.MessageResponse
import retrofit2.Call
import retrofit2.http.*

interface ItemsApi {

    @GET("/items")
    fun getItems(@Query("type") itemTypes: String?, @Query("category") categories: String?) : Call<List<ItemsResponse>>

    @GET("/items/search")
    fun searchItems(@Query("key") searchKey: String) : Call<List<ItemsResponse>>

    @POST("/item")
    fun newItem(@Header("Authorization") token: String?, @Body request: NewItemRequest) : Call<MessageResponse>


}