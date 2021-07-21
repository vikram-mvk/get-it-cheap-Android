package com.getitcheap.web_api.api_definition

import com.getitcheap.web_api.request.SigninRequest
import com.getitcheap.web_api.request.SignupRequest
import com.getitcheap.web_api.response.MessageResponse
import com.getitcheap.web_api.response.SigninResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UsersApi {

    @GET("/")
    fun isServerRunning() : Call<MessageResponse>

    @POST("/signin")
    fun Signin(@Body request: SigninRequest) : Call<SigninResponse>

    @POST("/signup")
    fun Signup(@Body request: SignupRequest) : Call<MessageResponse>

}