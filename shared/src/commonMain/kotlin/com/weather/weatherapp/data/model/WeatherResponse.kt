package com.weather.weatherapp.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("current_weather")
    val currentWeather: CurrentWeather?,
    @SerialName("current_weather_units")
    val currentWeatherUnits: CurrentWeatherUnits?,
    @SerialName("generationtime_ms")
    val generationtimeMs: Double?,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("timezone")
    val timezone: String?,
    @SerialName("timezone_abbreviation")
    val timezoneAbbreviation: String?,
    @SerialName("utc_offset_seconds")
    val utcOffsetSeconds: Int?
)