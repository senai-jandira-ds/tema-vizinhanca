package com.example.mobilevizinhaa.ui.theme.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

   // private const val BASE_URL = "http://10.0.2.2:8080/"
   private const val BASE_URL = "http://192.168.1.2:8080/"

    val authApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}