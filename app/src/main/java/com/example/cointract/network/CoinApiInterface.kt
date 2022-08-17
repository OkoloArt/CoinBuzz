package com.example.cointract.network

import androidx.lifecycle.LiveData
import com.example.cointract.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CoinApiInterface {

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

    @GET("markets")
    fun getMarketList(
        @Query("coinId") coinId: String,
    ): Call<List<MarketList>?>

    @GET("news")
    fun getNewsList(
        @Query("skip") skip: String,
        @Query("limit") limit:String
    ): Call<News?>

}