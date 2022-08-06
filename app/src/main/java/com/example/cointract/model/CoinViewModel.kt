package com.example.cointract.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.adapter.AssetListAdapter
import com.example.cointract.network.AssetApiInterface
import com.example.cointract.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoinViewModel : ViewModel(){

    private val _response = MutableLiveData<AssetsList>()
    val response: LiveData<AssetsList> get() = _response

    init {
        retrieveAssetListJson()
    }

    private fun retrieveAssetListJson() {
        val assetCall: Call<AssetsList?> = RetrofitInstance.retrofitInstance!!.create(
            AssetApiInterface::class.java
        ).getAssetList()
        assetCall.enqueue(object : Callback<AssetsList?> {
            override fun onResponse(call: Call<AssetsList?>, response: Response<AssetsList?>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    _response.value = response.body()
                }
            }
            override fun onFailure(call: Call<AssetsList?>, t: Throwable) {
            }
        })
    }
}