package com.weather.weatherapp.data.repository


import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.data.remote.ApiService
import com.weather.weatherapp.domain.repository.WeatherRepository

class WeatherRepositoryImpl(private val apiService: ApiService) : WeatherRepository {
    override suspend fun getWeather(lat: Double, lon: Double): WeatherResponse {
        return apiService.getWeather(lat,lon)
    }

}