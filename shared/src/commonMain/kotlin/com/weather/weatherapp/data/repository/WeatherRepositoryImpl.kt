package com.weather.weatherapp.data.repository


import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.data.remote.ApiService
import com.weather.weatherapp.domain.repository.WeatherRepository
import com.weather.weatherapp.utils.RestClientResult

class WeatherRepositoryImpl(private val apiService: ApiService) : WeatherRepository {
    override suspend fun getWeather(lat: Double, lon: Double): RestClientResult<WeatherResponse> {
        return apiService.getWeather(lat,lon)
    }

}