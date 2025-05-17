package com.moviles.courses.network

import retrofit2.Retrofit
import com.moviles.courses.common.Constants.API_BASE_URL
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: ApiServices by lazy{
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices::class.java)
    }
}