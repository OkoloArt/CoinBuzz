package com.example.cointract.network

import com.example.cointract.model.AssetSingle
import com.example.cointract.model.AssetsList
import com.example.cointract.model.SingleAsset
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface AssetApiInterface {

    @GET("assets")
    fun getAssetList(
    ): Call<AssetsList?>

    @GET("assets/{id}")
    fun getAssetSingle(
        @Path("id") assetId: String
    ): Call<AssetSingle?>
}