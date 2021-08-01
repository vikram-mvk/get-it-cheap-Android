package com.getitcheap.web_api

import com.getitcheap.web_api.api_definition.ItemsApi
import com.getitcheap.web_api.api_definition.UsersApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetroFitService {
    // 34.232.4.197
    // 10.0.2.2
    val SERVER_IP = "10.0.2.2"
    val PORT = "5000"

    // Android emulator to localhost = 10.0.2.2

    private val httpclient = OkHttpClient.Builder().callTimeout(5, TimeUnit.SECONDS).build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://$SERVER_IP:$PORT/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpclient)
        .build()

    val itemsApi: ItemsApi = retrofit.create(ItemsApi::class.java)

    val userApi: UsersApi = retrofit.create(UsersApi::class.java)

    fun<T> useApi(ApiDefinition: Class<T>): T {
        return retrofit.create(ApiDefinition)
    }
}