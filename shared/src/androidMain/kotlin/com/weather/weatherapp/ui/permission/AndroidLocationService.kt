package com.weather.weatherapp.ui.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AndroidLocationService(
    private val context: Context,
    private val launcher: ActivityResultLauncher<String>
) : LocationService {

    private val fusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(context) }

    override fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestLocationPermission(granted: (Boolean) -> Unit) {
        launcher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    @Suppress("MissingPermission")
    suspend fun getLocation(): Location = suspendCoroutine { continuation ->
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {location->
            location?.let {
                continuation.resume(Location(it.latitude,it.longitude))
            }
        }.addOnFailureListener {
            continuation.resumeWithException(Exception("Unable to get location."))
        }
    }
}

fun getCityNameFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0].locality ?: addresses[0].subAdminArea
        } else null
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}