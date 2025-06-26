package com.weather.weatherapp.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeather(
    @SerialName("interval")
    val interval: Int?,
    @SerialName("is_day")
    val isDay: Int?,
    @SerialName("temperature")
    val temperature: Double?,
    @SerialName("time")
    val time: String?,
    @SerialName("weathercode")
    val weathercode: Int?,
    @SerialName("winddirection")
    val winddirection: Int?,
    @SerialName("windspeed")
    val windspeed: Double?
)