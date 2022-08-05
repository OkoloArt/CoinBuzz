package com.example.cointract.network

import com.example.cointract.model.Assets
import retrofit2.Call
import retrofit2.http.GET


interface AssetApiInterface {

    @GET("assets")
    fun getAssetList(
    ): Call<Assets?>
}