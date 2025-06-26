package com.weather.weatherapp.data.local

import com.weather.weatherapp.domain.repository.WeatherLocalDataRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WeatherLocalRepositoryHelper: KoinComponent {

    private val weatherLocalDataRepository: WeatherLocalDataRepository by inject()
    fun getLocalRepository(): WeatherLocalDataRepository = weatherLocalDataRepository

}