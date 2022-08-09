package com.example.cointract.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object CoinCapRetrofitInstance {
    private const val COIN_CAP_BASE_URL = "https://api.coincap.io/v2/"
    private var retrofit: Retrofit? = null

    val coinCapRetrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(COIN_CAP_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

}