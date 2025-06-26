package com.weather.weatherapp.android

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weather.weatherapp.ui.permission.AndroidLocationService
import com.weather.weatherapp.ui.permission.getCityNameFromLatLng
import com.weather.weatherapp.ui.view_models.UiState
import com.weather.weatherapp.ui.view_models.WeatherViewModel
import com.weather.weatherapp.ui.view_models.getWeatherTypeFromCode
import com.weather.weatherapp.utils.getCurrentDayAndDate
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
                val cityName = remember { mutableStateOf<String?>(null) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,

                    ) {
                    val viewModel = koinViewModel<WeatherViewModel>()
                    var locationService by remember { mutableStateOf<AndroidLocationService?>(null) }
                    val scope = rememberCoroutineScope()
                    val permission =
                        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { bool ->
                            if (bool) {
                                scope.launch(Dispatchers.IO) {
                                    val location = locationService?.getLocation()
                                    location?.let {
                                        latitude.doubleValue = it.latitude
                                        longitude.doubleValue = it.longitude
                                    }
                                }
                            }
                        }

                    LaunchedEffect(latitude.doubleValue) {
                        if (latitude.doubleValue != 0.0 && longitude.doubleValue != 0.0) {
                            viewModel.getWeather(latitude.doubleValue, longitude.doubleValue)
                            cityName.value = getCityNameFromLatLng(
                                this@MainActivity,
                                latitude.doubleValue,
                                longitude.doubleValue
                            )
                        }
                    }
                    val context = LocalContext.current
                    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                    LaunchedEffect(Unit) {
                        permission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        locationService = AndroidLocationService(context, permission)
                        locationService?.requestLocationPermission { bool -> }
                    }
                    println("UIII--${uiState.value.data?.currentWeather?.windspeed}")

                    WeatherUi(uiState, cityName.value)

                }
            }
        }
    }


}

@Composable
fun WeatherUi(weatherState: State<UiState>, cityName: String?) {
    if (weatherState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator(color = Color.Black)
        }
    }

    weatherState.value.data?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier) {
                    Text(text = "Hello!!")
                    Text(text = getCurrentDayAndDate())
                }
                WeatherImage(
                    image = R.drawable.appicon,
                    modifier = Modifier.size(50.dp)
                )

            }

            Spacer(modifier = Modifier.padding(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.1f)
                ) {
                    cityName?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp),
                            fontSize = 30.sp
                        )
                    }

                    Text(
                        text = "${it.currentWeather?.temperature ?: "--"}°",
                        modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                        fontSize = 60.sp
                    )

                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    val weatherIcon = it.currentWeather?.weathercode?.let {
                        when (getWeatherTypeFromCode(it)) {
                            "Clear" -> R.drawable.clear
                            "Cloudy", "Foggy" -> R.drawable.cloudy
                            "Rainy" -> R.drawable.rainy
                            "Thunderstorm", "Thunderstorm with Hail" -> R.drawable.thunderstrom
                            else -> R.drawable.clear
                        }
                    } ?: R.drawable.clear
                    WeatherImage(
                        image = weatherIcon,
                        modifier = Modifier
                            .size(140.dp)
                            .padding(end = 10.dp)
                    )
                }


            }

            Spacer(modifier = Modifier.padding(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    WeatherImage(
                        image = R.drawable.windspeed,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Wind Speed : ${it.currentWeather?.windspeed?.toString() ?: "--"}km/h",
                        fontSize = 14.sp
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    WeatherImage(
                        image = R.drawable.winddirection,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Wind Direction : ${
                            it.currentWeather?.winddirection?.toString()
                                ?: "--"
                        }°", fontSize = 14.sp
                    )
                }
            }
        }
    }

    if (weatherState.value.error.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()){
            WeatherImage(
                image = R.drawable.nodatafound,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
    }
}


@Composable
fun WeatherImage(image: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(image),
        contentDescription = "",
        modifier = modifier
    )
}

