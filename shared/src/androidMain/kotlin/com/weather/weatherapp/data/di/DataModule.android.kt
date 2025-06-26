package com.weather.weatherapp.data.di

import androidx.room.Room
import com.weather.weatherapp.data.local.AppDatabase
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "weather.db"
        ).build()
    }
}