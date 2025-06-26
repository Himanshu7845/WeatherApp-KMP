package com.weather.weatherapp.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherUnits(
    @SerialName("interval")
    val interval: String?,
    @SerialName("is_day")
    val isDay: String?,
    @SerialName("temperature")
    val temperature: String?,
    @SerialName("time")
    val time: String?,
    @SerialName("weathercode")
    val weathercode: String?,
    @SerialName("winddirection")
    val winddirection: String?,
    @SerialName("windspeed")
    val windspeed: String?
)