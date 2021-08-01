package com.getitcheap.web_api.api_definition

import com.getitcheap.web_api.request.SigninRequest
import com.getitcheap.web_api.request.SignupRequest
import com.getitcheap.web_api.request.UpdateProfileRequest
import com.getitcheap.web_api.response.MessageResponse
import com.getitcheap.web_api.response.SigninResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UsersApi {

    @GET("/")
    fun isServerRunning() : Call<MessageResponse>

    @POST("/signin")
    fun signin(@Body request: SigninRequest) : Call<SigninResponse>

    @POST("/signup")
    fun signup(@Body request: SignupRequest) : Call<MessageResponse>

    @POST("/update-profile")
    fun updateProfile(@Header("Authorization") token: String, @Body request: UpdateProfileRequest) : Call<MessageResponse>

}