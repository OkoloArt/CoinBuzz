package com.example.cointract

import android.app.Application
import com.example.cointract.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin


class BaseApplication : Application(),KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {androidContext(this@BaseApplication)
            modules(listOf(applicationModule)) }
    }
}