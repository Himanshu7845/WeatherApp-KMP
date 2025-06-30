package com.weather.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert
    suspend fun insertWeatherData(weatherEntity: WeatherEntity):Long

    @Query("SELECT * FROM Weather LIMIT 1")
    fun fetchWeather(): Flow<WeatherEntity?>

    @Query("Delete from Weather")
    suspend fun clearDataBase()

}