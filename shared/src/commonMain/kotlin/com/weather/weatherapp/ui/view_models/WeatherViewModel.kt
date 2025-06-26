package com.weather.weatherapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.weatherapp.data.local.WeatherEntity
import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.domain.use_cases.GetWeatherUseCase
import com.weather.weatherapp.utils.NetworkErrorMessages.NO_INTERNET_CONNECTION
import com.weather.weatherapp.utils.RestClientResult
import com.weather.weatherapp.utils.common
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
) : ViewModel() {
    private var job: Job? = null

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow().common()


    fun getWeather(lat: Double, lon: Double, cityName: String?)  {
        launchWithHandlingException({ _, throwable ->
            println("API Call Failed or No More Data $throwable")
        }) {

            getWeatherUseCase.getWeather(lat,lon).collect { result ->
                when (result.status) {
                    RestClientResult.Status.LOADING -> {
                        _uiState.value = _uiState.value.copy(isLoading = true,error="")
                    }

                    RestClientResult.Status.SUCCESS -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            data = result.data
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
                                    if(it.isNotEmpty()){
                                        _uiState.value = _uiState.value.copy(
                                            isLoading = false,
                                            dataFromLocal = it
                                        )
                                    }
                                    else{
                                        _uiState.value = _uiState.value.copy(
                                            isLoading = false,
                                            error = "No data available in Local database fetch from internet"
                                        )
                                    }
                                }.catch { throwable->
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

data class UiState(
    val isLoading: Boolean = false,
    val data: WeatherResponse? = null,
    val dataFromLocal: List<WeatherEntity>? = null,
    val error: String = ""
)