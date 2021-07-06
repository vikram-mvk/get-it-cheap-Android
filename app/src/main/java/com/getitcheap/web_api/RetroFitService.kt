package com.getitcheap.web_api

import com.getitcheap.web_api.api_definition.ItemsApi
import com.getitcheap.web_api.api_definition.UsersApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitService {

    private val httpclient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpclient)
        .build()

    val itemsApi: ItemsApi = retrofit.create(ItemsApi::class.java)

    val userApi: UsersApi = retrofit.create(UsersApi::class.java)


    fun<T> useApi(ApiDefinition: Class<T>): T {
        return retrofit.create(ApiDefinition)
    }
}