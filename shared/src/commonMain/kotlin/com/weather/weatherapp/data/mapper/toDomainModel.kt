package com.weather.weatherapp.data.mapper

import com.weather.weatherapp.data.local.WeatherEntity
import com.weather.weatherapp.data.model.WeatherResponse

fun WeatherResponse.toEntity(cityName: String?): WeatherEntity {
    return WeatherEntity(
        temperature = currentWeather?.temperature ?: 0.0,
        weatherCode = currentWeather?.weathercode ?: 0,
        windSpeed = currentWeather?.windspeed ?: 0.0,
        windDirection = currentWeather?.winddirection ?: 0,
        cityName = cityName ?: ""
    )
}
