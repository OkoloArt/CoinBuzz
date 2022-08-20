package com.example.cointract.model

import com.google.gson.annotations.SerializedName

data class AssetsList(@SerializedName("data") var data: List<AssetList>)

data class AssetSingle(@SerializedName("data") var data : SingleAsset)

data class Exchanges(@SerializedName("data") var data: List<ExchangeList>)

data class Candles(@SerializedName("data") var data: List<CandlesData>)

data class News(@SerializedName("news") var news: List<NewsList>)

data class Coins(@SerializedName("coins") var coins: List<CoinStatList>)


