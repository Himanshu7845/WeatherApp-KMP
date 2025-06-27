package com.weather.weatherapp.domain.use_cases

import com.weather.weatherapp.data.local.WeatherEntity
import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.domain.repository.WeatherLocalDataRepository
import com.weather.weatherapp.domain.repository.WeatherRepository
import com.weather.weatherapp.utils.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

open class GetWeatherUseCase(private val weatherRepository: WeatherRepository, private val weatherLocalDataRepository: WeatherLocalDataRepository) {

    /** get weather details API call. */
    open fun getWeather(lat: Double, lon: Double): Flow<RestClientResult<WeatherResponse?>> = flow {
            emit(RestClientResult.loading())
            emit(weatherRepository.getWeather(lat,lon))
            emit(RestClientResult.idle())
        }

    /** insert weather details from local database. */
    open  suspend fun insertWeatherDataInRoom(entity: WeatherEntity): Long {
        return weatherLocalDataRepository.insertGrowth(entity)

    }
    /** clear weather details from local database. */
    open  suspend fun clearData() {
        return weatherLocalDataRepository.clearDataBase()
    }

    /** get weather details from local database. */
    open  suspend fun getWeatherDetailsFromLocal(): Flow<List<WeatherEntity>> {
        return weatherLocalDataRepository.fetchWeather()
    }
}