package com.getitcheap.web_api.api_definition

import com.getitcheap.web_api.request.SigninRequest
import com.getitcheap.web_api.request.SignupRequest
import com.getitcheap.web_api.response.MessageResponse
import com.getitcheap.web_api.response.SigninResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UsersApi {
    @POST("/signin")
    fun Signin(@Body request: SigninRequest) : Call<SigninResponse>

    @POST("/signup")
    fun Signup(@Body request: SignupRequest) : Call<MessageResponse>

}