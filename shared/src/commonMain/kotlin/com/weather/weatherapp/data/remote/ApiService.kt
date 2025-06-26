package com.weather.weatherapp.data.remote

import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.utils.BaseDataSource
import com.weather.weatherapp.utils.BaseUrl.BASE_URL
import com.weather.weatherapp.utils.RestClientResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType


class ApiService(private val httpClient: HttpClient):BaseDataSource() {
    // https://api.open-meteo.com/v1/forecast?latitude=40.71&longitude=-74.01&current_weather=true
    suspend fun getWeather(lat: Double, lng: Double):RestClientResult<WeatherResponse> {
        return getResult {
            httpClient.get("${BASE_URL}forecast?latitude=$lat&longitude=$lng&current_weather=true") {
                contentType(ContentType.Application.Json)
            }
        }
    }

}