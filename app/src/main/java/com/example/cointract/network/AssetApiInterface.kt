package com.example.cointract.network

import com.example.cointract.model.AssetSingle
import com.example.cointract.model.AssetsList
import com.example.cointract.model.Candles
import com.example.cointract.model.Exchanges
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface AssetApiInterface {

    @GET("assets")
    fun getAssetList(
    ): Call<AssetsList?>

    @GET("assets/{id}")
    fun getAssetSingle(
        @Path("id") assetId: String,
    ): Call<AssetSingle?>

    @GET("exchanges")
    fun getExchangeList(
    ): Call<Exchanges?>

    @GET("candles")
    fun getCandleList(
        @Query("exchange") exchange: String,
        @Query("interval") interval: String,
        @Query("baseId") baseId: String,
        @Query("quoteId") quoteId: String,
    ): Call<Candles?>
}