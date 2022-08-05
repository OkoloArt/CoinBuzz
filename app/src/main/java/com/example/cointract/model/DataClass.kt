package com.example.cointract.model

import com.google.gson.annotations.SerializedName

data class Assets(@SerializedName("data") var data: List<AssetResults>)