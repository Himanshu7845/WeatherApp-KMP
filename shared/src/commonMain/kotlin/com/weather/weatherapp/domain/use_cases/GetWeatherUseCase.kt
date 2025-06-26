package com.weather.weatherapp.domain.use_cases

import com.weather.weatherapp.data.local.WeatherEntity
import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.domain.repository.WeatherLocalDataRepository
import com.weather.weatherapp.domain.repository.WeatherRepository
import com.weather.weatherapp.utils.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetWeatherUseCase(private val weatherRepository: WeatherRepository,private val weatherLocalDataRepository: WeatherLocalDataRepository) {

    /** get weather details API call. */
     fun getWeather(lat: Double, lon: Double): Flow<RestClientResult<WeatherResponse?>> =
        flow {
            emit(RestClientResult.loading())
            emit(weatherRepository.getWeather(lat,lon))
            emit(RestClientResult.idle())
        }

    /** get weather details from local database. */
    suspend fun insertWeatherDataInRoom(entity: WeatherEntity): Long {
        return weatherLocalDataRepository.insertGrowth(entity)
    }
}