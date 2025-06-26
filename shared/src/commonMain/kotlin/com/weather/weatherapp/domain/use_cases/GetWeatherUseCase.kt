package com.weather.weatherapp.domain.use_cases

import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.domain.repository.WeatherRepository
import com.weather.weatherapp.utils.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetWeatherUseCase(private val weatherRepository: WeatherRepository) {

    /** get weather details API call. */
     fun getWeather(lat: Double, lon: Double): Flow<RestClientResult<WeatherResponse?>> =
        flow {
            emit(RestClientResult.loading())
            emit(weatherRepository.getWeather(lat,lon))
            emit(RestClientResult.idle())
        }

}