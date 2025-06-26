package com.weather.weatherapp.ui.di

import com.weather.weatherapp.ui.view_models.WeatherViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module

private val viewModelModule = module {
    single { WeatherViewModel(get()) }
}

actual fun sharedViewModelModules(): Module = viewModelModule

object ProvideViewModel : KoinComponent {

    fun getWeatherViewModel() = get<WeatherViewModel>()

}
