package com.weather.weatherapp.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getCurrentDayAndDate(): String {
    val now = Clock.System.now()
    val dateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val date = dateTime.date

    val dayOfWeek = date.dayOfWeek.name.lowercase()
        .replaceFirstChar { it.uppercase() }

    val day = date.dayOfMonth.toString().padStart(2, '0')

    val month = date.month.name.lowercase()
        .replaceFirstChar { it.uppercase() }

    val year = date.year

    return "$dayOfWeek, $day $month $year"
}