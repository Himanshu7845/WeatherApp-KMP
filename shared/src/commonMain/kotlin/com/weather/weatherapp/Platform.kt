package com.weather.weatherapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform