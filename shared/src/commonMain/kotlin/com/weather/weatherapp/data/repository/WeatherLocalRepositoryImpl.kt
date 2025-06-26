package com.weather.weatherapp.data.repository

import com.weather.weatherapp.data.local.WeatherDao
import com.weather.weatherapp.data.local.WeatherEntity
import com.weather.weatherapp.domain.repository.WeatherLocalDataRepository
import kotlinx.coroutines.flow.Flow

class WeatherLocalRepositoryImpl(private val weatherDao: WeatherDao): WeatherLocalDataRepository {
    override suspend fun insertGrowth(entity: WeatherEntity): Long {
       return weatherDao.insertWeatherData(entity)
    }

    override suspend fun fetchWeather(): Flow<List<WeatherEntity>> {
        return  weatherDao.fetchWeather()

    }


}