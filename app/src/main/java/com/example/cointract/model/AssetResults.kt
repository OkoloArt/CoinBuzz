package com.example.cointract.model

import com.google.gson.annotations.SerializedName

data class AssetList(

    @SerializedName("id")
    val assetId: String,

    @SerializedName("name")
    val assetName: String,

    @SerializedName("priceUsd")
    val assetPriceUsd: String,

    @SerializedName("rank")
    val assetRank: String,

    @SerializedName("changePercent24Hr")
    val assetChange24Hr: String,

    @SerializedName("marketCapUsd")
    val assetMCap: String,
)

data class SingleAsset(
    @SerializedName("id")
    val assetId: String,

    @SerializedName("name")
    val assetName: String,

    @SerializedName("priceUsd")
    val assetPriceUsd: String,

    @SerializedName("changePercent24Hr")
    val assetChange24Hr: String,

    @SerializedName("symbol")
    val assetSymbol: String,

    @SerializedName("marketCapUsd")
    val assetMCap: String,

    @SerializedName("volumeUsd24Hr")
    val assetVolumeUsd24Hr: String,

    @SerializedName("maxSupply")
    val assetMaxSupply: String,

    @SerializedName("supply")
    val assetCirculatingSupply: String,

    @SerializedName("rank")
    val assetRank: String,

    )

data class ExchangeList(

    @SerializedName("name")
    val exchangeName: String,

    @SerializedName("volumeUsd")
    val exchangeVolumeUsd: String,

    @SerializedName("rank")
    val exchangeRank: String,

    @SerializedName("percentTotalVolume")
    val exchangePercentVolume: String,

    @SerializedName("tradingPairs")
    val exchangePairs: String,

    @SerializedName("exchangeUrl")
    val exchangeUrl: String,
)