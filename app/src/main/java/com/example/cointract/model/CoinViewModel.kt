package com.example.cointract.model

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cointract.adapter.MarketAdapter
import com.example.cointract.network.CoinApiInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


class CoinViewModel : ViewModel(), KoinComponent {

    private val coinStatsInstance by inject<CoinApiInterface>(named("CoinStats"))
    private val coinCapInstance by inject<CoinApiInterface>(named("CoinCap"))

    private val _assetId = MutableLiveData<String>()
    val assetId: LiveData<String> get() = _assetId

    private var _responseNews = MutableLiveData<News>()
    val responseNews: LiveData<News> get() = _responseNews

    private var _responseExchange = MutableLiveData<Exchanges>()
    val responseExchange: LiveData<Exchanges> get() = _responseExchange

    private var _responseAssetList = MutableLiveData<AssetsList>()
    val responseAssetList: LiveData<AssetsList> get() = _responseAssetList

    private var _responseCoinList = MutableLiveData<Coins>()
    val responseCoinList: LiveData<Coins> get() = _responseCoinList

    fun setAssetId(id: String) {
        _assetId.value = id
    }

    init {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    retrieveAssetListJson()
                    retrieveExchangeListJson()
                }
            }
        }, 0, 10000)
        retrieveNewsListJson()
        retrieveCoinListJson()
    }

    private fun retrieveNewsListJson() {
        CoroutineScope(IO).launch {
            val assetCall: Call<News?> = coinStatsInstance.getNewsList(SKIP, LIMIT)
            assetCall.enqueue(object : Callback<News?> {
                override fun onResponse(call: Call<News?>, response: Response<News?>) {
                    if (response.isSuccessful && response.body()?.news != null) {
                        _responseNews.value = response.body()
                    }
                }

                override fun onFailure(call: Call<News?>, t: Throwable) {
//                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun retrieveCoinListJson() {
        CoroutineScope(IO).launch {
            val assetCall: Call<Coins?> = coinStatsInstance.getCoinsList()
            assetCall.enqueue(object : Callback<Coins?> {
                override fun onResponse(call: Call<Coins?>, response: Response<Coins?>) {
                    if (response.isSuccessful && response.body()?.coins != null) {
                        _responseCoinList.value = response.body()
                    }
                }

                override fun onFailure(call: Call<Coins?>, t: Throwable) {

                }
            })
        }
    }

    private fun retrieveExchangeListJson() {
        CoroutineScope(IO).launch {
            val assetCall: Call<Exchanges?> =coinCapInstance.getExchangeList()
            assetCall.enqueue(object : Callback<Exchanges?> {
                override fun onResponse(call: Call<Exchanges?>, response: Response<Exchanges?>) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        _responseExchange.value = response.body()
                    }
                }
                override fun onFailure(call: Call<Exchanges?>, t: Throwable) {
                }
            })
        }
    }

    private fun retrieveAssetListJson() {
        CoroutineScope(IO).launch {
            val assetCall: Call<AssetsList?> = coinCapInstance.getAssetList()
            assetCall.enqueue(object : Callback<AssetsList?> {
                override fun onResponse(call: Call<AssetsList?>, response: Response<AssetsList?>) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        _responseAssetList.value = response.body()
                    }
                }
                override fun onFailure(call: Call<AssetsList?>, t: Throwable) {
                }
            })
        }
    }

    companion object {
        private const val SKIP = "0"
        private const val LIMIT = ""
    }
}