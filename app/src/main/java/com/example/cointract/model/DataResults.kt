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

data class CandlesData(

    @SerializedName("open")
    val candleOpen: String,

    @SerializedName("close")
    val candleClose: String,

    @SerializedName("high")
    val candleHigh: String,

    @SerializedName("low")
    val candleLow: String,
)

data class MarketList(

    @SerializedName("exchange")
    val exchange: String,

    @SerializedName("pair")
    val pair: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("volume")
    val volume: String,
)

data class NewsList(

    @SerializedName("title")
    val news_title: String,

    @SerializedName("imgURL")
    val news_image: String,

    @SerializedName("source")
    val news_source: String,

    @SerializedName("link")
    val news_link: String,

    )

data class CoinStatList(

    @SerializedName("id")
    val asset_id: String,

    @SerializedName("websiteUrl")
    val asset_Url: String,

    )

