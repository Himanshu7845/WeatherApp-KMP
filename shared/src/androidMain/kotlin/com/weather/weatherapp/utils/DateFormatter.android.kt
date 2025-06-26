package com.weather.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun dateFormatter(millis: Long): String {
    val date = Date(millis*1000)
    val formatter = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
    return formatter.format(date)
}