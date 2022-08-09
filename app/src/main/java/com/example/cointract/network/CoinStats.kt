package com.example.cointract.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstanceTwo {
    private const val COIN_STATS_BASE_URL = "https://api.coinstats.app/public/v1/"
    private var retrofit: Retrofit? = null

    val coinStatsRetrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(COIN_STATS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}