package com.weather.weatherapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.weatherapp.data.local.WeatherEntity
import com.weather.weatherapp.data.mapper.toEntity
import com.weather.weatherapp.domain.use_cases.GetWeatherUseCase
import com.weather.weatherapp.utils.NetworkErrorMessages.NO_INTERNET_CONNECTION
import com.weather.weatherapp.utils.RestClientResult
import com.weather.weatherapp.utils.common
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow().common()


    fun getWeather(lat: Double, lon: Double, cityName: String?)  {
       viewModelScope.launch {
           getWeatherUseCase.getWeather(lat,lon).collect { result ->
               when (result.status) {
                   RestClientResult.Status.LOADING -> {
                       _uiState.value = _uiState.value.copy(isLoading = true,error="")
                   }

                   RestClientResult.Status.SUCCESS -> {
                       _uiState.value = _uiState.value.copy(
                           isLoading = false,
                           data = result.data?.toEntity(cityName)
                       )
                       result.data?.let {
                           getWeatherUseCase.clearData()
                           getWeatherUseCase.insertWeatherDataInRoom(WeatherEntity(
                               cityName = cityName ?: "",
                               weatherCode = it.currentWeather?.weathercode?:0,
                               windSpeed = it.currentWeather?.windspeed ?: 0.0,
                               windDirection = it.currentWeather?.winddirection ?: 0,
                               temperature = it.currentWeather?.temperature ?: 0.0,
                           ))
                       }
                   }

                   RestClientResult.Status.ERROR -> {
                       if (!result.errorMessage.isNullOrEmpty()) {
                           if(result.errorMessage==NO_INTERNET_CONNECTION){
                               getWeatherUseCase.getWeatherDetailsFromLocal().onStart {
                                   _uiState.value = _uiState.value.copy(isLoading = true,error="")
                               }.onEach {
                                   println("IT va;lue==$it")
                                   if ( it != null) {
                                       _uiState.value = _uiState.value.copy(
                                           isLoading = false,
                                           data = it
                                       )
                                   } else {
                                       _uiState.value = _uiState.value.copy(
                                           isLoading = false,
                                           error = "No data available in Local database, fetch from internet"
                                       )
                                   }
                               }.catch { throwable->
                                   println("ERROR-${throwable.message.toString()}")
                                   _uiState.value = _uiState.value.copy(isLoading = false,error=throwable.message.toString())
                               }.collect()
                           }
                           else{
                               _uiState.value = _uiState.value.copy(isLoading = false,error=result.errorMessage)
                           }

                       }
                   }

                   RestClientResult.Status.IDLE -> {

                   }
               }
           }
       }
    }
}
data class UiState(
    val isLoading: Boolean = false,
    val data: WeatherEntity? = null,
    val error: String = ""
)