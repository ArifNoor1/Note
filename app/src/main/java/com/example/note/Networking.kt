package com.example.note

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Networking {
    val BASE_URL = "https://dummyjson.com/"
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NetworkService::class.java)
}