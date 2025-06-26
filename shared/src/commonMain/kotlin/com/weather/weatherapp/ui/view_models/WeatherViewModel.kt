package com.weather.weatherapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.domain.use_cases.GetWeatherUseCase
import com.weather.weatherapp.utils.common
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow().common()

    fun getWeather(lat: Double, lon: Double) = viewModelScope.launch {
        val response = getWeatherUseCase(lat, lon)
        _uiState.update { UiState(isLoading = true) }
        if (response.isSuccess) {
            _uiState.update { UiState(data = response.getOrNull()) }
        } else {
            _uiState.update { UiState(error = response.exceptionOrNull()?.message.toString()) }
        }
    }

}


data class UiState(
    val isLoading: Boolean = false,
    val data: WeatherResponse? = null,
    val error: String = ""
)