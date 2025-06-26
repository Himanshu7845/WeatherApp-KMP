package com.weather.weatherapp.data.di

import androidx.room.RoomDatabase
import com.weather.weatherapp.data.local.AppDatabase
import com.weather.weatherapp.data.local.getDatabaseBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
}