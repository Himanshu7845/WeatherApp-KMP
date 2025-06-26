package com.weather.weatherapp.data.remote

import com.weather.weatherapp.data.model.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.path


class ApiService(private val client: HttpClient) {
    // https://api.open-meteo.com/v1/forecast?latitude=40.71&longitude=-74.01&current_weather=true
    suspend fun getWeather(lat: Double, lng: Double): WeatherResponse {
        return client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.open-meteo.com"
                path("v1", "forecast")
                parameters.append("latitude", lat.toString())
                parameters.append("longitude", lng.toString())
                parameters.append("current_weather", "true")
            }
        }.body<WeatherResponse>()
    }

}