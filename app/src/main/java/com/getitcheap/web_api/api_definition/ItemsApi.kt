package com.getitcheap.web_api.api_definition

import com.getitcheap.web_api.request.NewItemRequest
import com.getitcheap.web_api.response.ItemsResponse
import com.getitcheap.web_api.response.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ItemsApi {

    @GET("/items")
    fun getItems(@Query("type") itemTypes: String?, @Query("category") categories: String?) : Call<List<ItemsResponse>>

    @GET("/items/search")
    fun searchItems(@Query("key") searchKey: String) : Call<List<ItemsResponse>>

    @POST("/item")
    @Multipart
    fun newItem(@Header("Authorization") token: String,
                @Part("itemName") itemName: RequestBody,
                @Part("description") description: RequestBody,
                @Part("category") category: RequestBody,
                @Part("itemType") itemType: RequestBody,
                @Part("price") price: RequestBody,
                @Part image: MultipartBody.Part?,
                @Part("rentalBasis") rentalBasis: RequestBody?,
                @Part("userId") userId: RequestBody,
                @Part("username") username: RequestBody,
                @Part("itemLocation") itemLocation: RequestBody,
                @Part("contact") contact: RequestBody
    ) : Call<MessageResponse>

    @POST("/upload")
    @Multipart
    fun uploadImage(@Part part: Part) : Call<MessageResponse>

}