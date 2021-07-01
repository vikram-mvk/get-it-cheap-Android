package com.getitcheap.web_api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitService {

    private val httpclient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://ec2-52-202-39-88.compute-1.amazonaws.com:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpclient)
        .build()

        fun<T> useApi(ApiDefinition: Class<T>): T {
            return retrofit.create(ApiDefinition)
        }
}