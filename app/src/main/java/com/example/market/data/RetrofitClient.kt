package com.example.market.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.110.153:8080/") // your server IP and port
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val recapApi: RecapApiService by lazy {
        retrofit.create(RecapApiService::class.java)
    }

    val modelConversionApi: ModelConversionApiService by lazy {
        retrofit.create(ModelConversionApiService::class.java)
    }

    val modelApi: ModelApiService by lazy {
        retrofit.create(ModelApiService::class.java)
    }


}