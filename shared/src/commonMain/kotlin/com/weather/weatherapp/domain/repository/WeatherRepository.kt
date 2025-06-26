package com.weather.weatherapp.domain.repository

import com.weather.weatherapp.data.model.WeatherResponse

interface WeatherRepository {

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse

}