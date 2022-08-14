package com.example.cointract.di


import com.example.cointract.datastore.SettingsManager
import com.example.cointract.model.CoinViewModel
import com.example.cointract.network.CoinApiInterface
import com.example.cointract.network.CoinCapRetrofitInstance
import com.example.cointract.network.CoinStatsRetrofitInstance
import com.example.cointract.utils.NetworkConnectivityObserver

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val applicationModule = module(override = true) {

    single { SettingsManager(androidContext()) }

    single { NetworkConnectivityObserver(androidContext()) }

    single { CoinCapRetrofitInstance }

    single { CoinStatsRetrofitInstance }

    single {
        get<CoinCapRetrofitInstance>().coinStatsRetrofitInstance?.create(CoinApiInterface::class.java)
    }

    viewModel {
        CoinViewModel()
    }

}


