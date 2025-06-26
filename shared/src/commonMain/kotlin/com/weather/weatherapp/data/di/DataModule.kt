package com.weather.weatherapp.data.di

import com.weather.weatherapp.data.remote.ApiService
import com.weather.weatherapp.data.remote.KtorClient
import com.weather.weatherapp.data.repository.WeatherRepositoryImpl
import com.weather.weatherapp.domain.repository.WeatherRepository
import org.koin.dsl.module

val dataModule =  module{
    factory { ApiService(KtorClient.client) }
    factory<WeatherRepository> { WeatherRepositoryImpl(get()) }
}