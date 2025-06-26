package com.weather.weatherapp.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone

actual fun dateFormatter(millis: Long): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "dd MMM hh:mm a"
        timeZone = NSTimeZone.localTimeZone
    }
    val date = NSDate(millis.times(1000).toDouble() / 1000)
    return formatter.stringFromDate(date)
}