package com.weather.weatherapp.domain.repository

import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.utils.RestClientResult

interface WeatherRepository {

    suspend fun getWeather(lat: Double, lon: Double): RestClientResult<WeatherResponse>

}