package com.weather.weatherapp.ui.di

import com.weather.weatherapp.ui.view_models.WeatherViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { WeatherViewModel(get()) }
}

actual fun sharedViewModelModules(): Module = viewModelModule