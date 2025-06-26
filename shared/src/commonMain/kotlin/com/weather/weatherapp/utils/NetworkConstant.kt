package com.weather.weatherapp.utils


object BaseUrl{
    const val BASE_URL="https://api.open-meteo.com/v1/"
}
object NetworkErrorMessages {
    const val BAD_REQUEST = "Bad request"
    const val SOME_ERROR_OCCURRED = "Something went wrong"
    const val APP_UNDER_MAINTENANCE = "App under maintenance"
}
object NetworkErrorCodes {
    const val BAD_REQUEST = 400
    const val NOT_FOUND = 404
    const val INTERNET_NOT_WORKING = 712
    const val UNKNOWN_ERROR_OCCURRED = 713
    const val NETWORK_CALL_CANCELLED = 714
    const val DATA_SERIALIZATION_ERROR = 715
}
