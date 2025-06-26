package com.weather.weatherapp.data.di

import com.weather.weatherapp.data.local.AppDatabase
import com.weather.weatherapp.data.local.getDao
import com.weather.weatherapp.data.local.getRoomDatabase
import com.weather.weatherapp.data.remote.ApiService
import com.weather.weatherapp.data.remote.KtorClient
import com.weather.weatherapp.data.repository.WeatherLocalRepositoryImpl
import com.weather.weatherapp.data.repository.WeatherRepositoryImpl
import com.weather.weatherapp.domain.repository.WeatherLocalDataRepository
import com.weather.weatherapp.domain.repository.WeatherRepository
import com.weather.weatherapp.domain.use_cases.GetWeatherUseCase
import com.weather.weatherapp.ui.view_models.WeatherViewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.coroutines.EmptyCoroutineContext.get

expect fun platformModule(): Module

val dataModule = module {
    factory { ApiService(KtorClient.client) }
    factory<WeatherRepository> { WeatherRepositoryImpl(get()) }
    factory<WeatherLocalDataRepository> { WeatherLocalRepositoryImpl(get()) }
    single { get<AppDatabase>().getWeatherDao() }
}