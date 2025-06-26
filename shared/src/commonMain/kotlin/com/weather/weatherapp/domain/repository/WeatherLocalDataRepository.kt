package com.weather.weatherapp.domain.repository

import com.weather.weatherapp.data.local.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataRepository {
    suspend fun insertGrowth(entity: WeatherEntity):Long
    suspend fun fetchWeather(): Flow<List<WeatherEntity>>
    suspend fun clearDataBase()

}