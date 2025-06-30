package com.weather.weatherapp.utils
fun getWeatherTypeFromCode(code: Int): String = when (code) {
        0 -> "Clear"
        1, 2, 3 -> "Cloudy"
        in 45..48 -> "Foggy"
        in 51..67 -> "Rainy"
        in 71..77 -> "Snowy"
        in 80..82 -> "Rainy"
        95 -> "Thunderstorm"
        in 96..99 -> "Thunderstorm with Hail"
        else -> "Unknown"
    }