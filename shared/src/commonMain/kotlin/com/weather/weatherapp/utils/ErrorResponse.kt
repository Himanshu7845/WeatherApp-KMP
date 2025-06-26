package com.weather.weatherapp.utils

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val message: String,
    val error: String? = null,
    val statusCode: Int? = null,
)