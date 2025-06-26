package com.weather.weatherapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.weatherapp.data.local.WeatherEntity
import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.domain.use_cases.GetWeatherUseCase
import com.weather.weatherapp.utils.RestClientResult
import com.weather.weatherapp.utils.common
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
) : ViewModel() {
    private var job: Job? = null

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow().common()


    fun getWeather(lat: Double, lon: Double)  {
        println("LatLngVM-->$lat,$lon")
        launchWithHandlingException({ _, throwable ->
            println("API Call Failed or No More Data $throwable")
        }) {

            getWeatherUseCase.getWeather(lat,lon).collect { result ->
                when (result.status) {
                    RestClientResult.Status.LOADING -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }

                    RestClientResult.Status.SUCCESS -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            data = result.data
                        )
                        result.data?.let {
                            getWeatherUseCase.insertWeatherDataInRoom(WeatherEntity(
                                latitude = it.latitude?:0.0,
                                longitude =  it.longitude?:0.0,
                                date = 10L,
                                currentWeatherTemperature = "27"
                            ))
                        }
                    }

                    RestClientResult.Status.ERROR -> {
                        if (!result.errorMessage.isNullOrEmpty()) {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                        }
                    }

                    RestClientResult.Status.IDLE -> {

                    }
                }
            }
        }
    }

    fun launchWithHandlingException(
        handler: (CoroutineContext, Throwable) -> Unit,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        job =
            viewModelScope.launch(
                context = SupervisorJob() + CoroutineExceptionHandler(handler),
                block = block,
            )
    }
}


data class UiState(
    val isLoading: Boolean = false,
    val data: WeatherResponse? = null,
    val error: String = ""
)