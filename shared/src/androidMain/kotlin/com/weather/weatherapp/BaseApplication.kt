package com.weather.weatherapp

import android.app.Application
import com.weather.weatherapp.data.di.dataModule
import com.weather.weatherapp.data.di.platformModule
import com.weather.weatherapp.domain.di.domainModule
import com.weather.weatherapp.ui.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules( listOf(
                dataModule,
                domainModule,
                viewModelModule,
                platformModule() // ✅ include this!
            ))
        }
    }
}