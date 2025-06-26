package com.weather.weatherapp.ui.permission

interface LocationService {

    fun isPermissionGranted(): Boolean

    fun requestLocationPermission(granted: (Boolean) -> Unit)

}

data class Location(val latitude: Double, val longitude: Double)