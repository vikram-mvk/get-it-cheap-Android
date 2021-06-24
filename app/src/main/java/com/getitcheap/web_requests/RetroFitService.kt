package com.getitcheap.web_requests

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

        fun<T> useApi(ApiDefinition: Class<T>): T {
            return retrofit.create(ApiDefinition)
        }
}