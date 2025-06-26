package com.weather.weatherapp.domain.use_cases

import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.domain.repository.WeatherRepository

class GetWeatherUseCase(private val weatherRepository: WeatherRepository) {

    suspend operator fun invoke(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val response = weatherRepository.getWeather(lat, lon)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}