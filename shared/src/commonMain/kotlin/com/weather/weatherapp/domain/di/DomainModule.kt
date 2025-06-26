package com.weather.weatherapp.domain.di

import com.weather.weatherapp.domain.use_cases.GetWeatherUseCase
import org.koin.dsl.module

val domainModule = module{
    factory { GetWeatherUseCase(get(),get()) }
}