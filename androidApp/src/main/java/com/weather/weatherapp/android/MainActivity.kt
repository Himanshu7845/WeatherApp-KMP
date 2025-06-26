package com.weather.weatherapp.android

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weather.weatherapp.ui.permission.AndroidLocationService
import com.weather.weatherapp.ui.view_models.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val latitude = remember { mutableDoubleStateOf(0.0) }
                val longitude = remember { mutableDoubleStateOf(0.0) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = koinViewModel<WeatherViewModel>()
                    var locationService by remember { mutableStateOf<AndroidLocationService?>(null) }
                    val scope = rememberCoroutineScope()
                    val permission = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { bool ->
                            if (bool) {
                                scope.launch(Dispatchers.IO) {
                                    val location = locationService?.getLocation()
                                    location?.let {
                                        latitude.doubleValue=it.latitude
                                        longitude.doubleValue=it.longitude
                                    }
                                }
                            }
                        }

                    LaunchedEffect(latitude.doubleValue) {
                        if(latitude.doubleValue!=0.0 && longitude.doubleValue!=0.0){
                            viewModel.getWeather(latitude.doubleValue,longitude.doubleValue)
                        }
                    }
                    val context = LocalContext.current
                    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

                    LaunchedEffect(Unit) {
                        permission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        locationService = AndroidLocationService(context, permission)
                        locationService?.requestLocationPermission { bool -> }
                    }
                    uiState.data?.let {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            Text(text ="${it.currentWeather?.time}", color = Color.Black)
                        }
                    }

                }
            }
        }
    }
}


@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {


    }
}
