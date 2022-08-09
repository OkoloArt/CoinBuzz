package com.example.cointract.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class CoinViewModel() : ViewModel() {

    private val _assetId = MutableLiveData<String>()
    val assetId: LiveData<String> get() = _assetId

    fun setAssetId(id: String) {
        _assetId.value = id
    }
}