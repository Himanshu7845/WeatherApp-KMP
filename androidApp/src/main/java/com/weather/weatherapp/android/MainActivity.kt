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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weather.weatherapp.ui.permission.AndroidLocationService
import com.weather.weatherapp.ui.permission.getCityNameFromLatLng
import com.weather.weatherapp.ui.view_models.UiState
import com.weather.weatherapp.ui.view_models.WeatherViewModel
import com.weather.weatherapp.ui.view_models.getWeatherTypeFromCode
import com.weather.weatherapp.utils.NetworkErrorMessages.NO_INTERNET_CONNECTION
import com.weather.weatherapp.utils.getCurrentDayAndDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val latitude = remember { mutableDoubleStateOf(0.0) }
                val longitude = remember { mutableDoubleStateOf(0.0) }
                val onRetryNetworkCall = remember { mutableStateOf(false) }
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

                    LaunchedEffect(Unit) {
                        snapshotFlow {
                            Triple(latitude.doubleValue, longitude.doubleValue, onRetryNetworkCall.value)
                        }.collectLatest { (lat, lng, shouldRetry) ->
                            if (lat != 0.0 && lng != 0.0 && (shouldRetry || cityName.value.isNullOrEmpty())) {
                                cityName.value = getCityNameFromLatLng(this@MainActivity, lat, lng)
                                viewModel.getWeather(lat, lng, cityName.value)
                                onRetryNetworkCall.value = false
                            }
                        }
                    }

                    val context = LocalContext.current
                    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
                    LaunchedEffect(Unit) {
                        permission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        locationService = AndroidLocationService(context, permission)
                        locationService?.requestLocationPermission { bool -> }
                    }
                    WeatherUi(
                        uiState,
                        cityName.value,
                        onRetryNetworkCall = {
                            onRetryNetworkCall.value=true
                        }
                    )

                }
            }
        }
    }


}

@Composable
fun WeatherUi(weatherState: State<UiState>, cityName: String?,onRetryNetworkCall:()->Unit) {
    if (weatherState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Black)
        }
    }
    weatherState.value.data?.let {
        WeatherDetails(
            cityName = cityName,
            temperature = it.currentWeather?.temperature,
            weatherCode = it.currentWeather?.weathercode,
            windSpeed = it.currentWeather?.windspeed,
            windDirection = it.currentWeather?.winddirection
        )
    }
    weatherState.value.dataFromLocal?.let {
        if (it.isNotEmpty()) {
            WeatherDetails(
                cityName = cityName,
                temperature = it[0].temperature,
                weatherCode = it[0].weatherCode,
                windSpeed = it[0].windSpeed,
                windDirection = it[0].windDirection
            )

        }
    }
    if (weatherState.value.error.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                WeatherImage(
                    image = R.drawable.cloudy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(40.dp)
                )
                if (weatherState.value.error != NO_INTERNET_CONNECTION) {
                    Text(
                        text = weatherState.value.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 10.dp,end=10.dp)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                   Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                       Button(onClick = {
                           onRetryNetworkCall.invoke()
                       }, colors = ButtonDefaults.buttonColors(Color.Black), shape = RoundedCornerShape(2.dp)) {
                           Text("Retry", color = Color.White)
                       }
                   }
                }
            }
        }
    }
}

@Composable
fun WeatherDetails(
    cityName: String?,
    temperature: Double?,
    weatherCode: Int?,
    windSpeed: Double?,
    windDirection: Int?
) {
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
                    text = "${temperature ?: "--"}°",
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
                val weatherIcon = weatherCode?.let {
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
                    text = "Wind Speed : ${windSpeed?.toString() ?: "--"}km/h",
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
                        windDirection?.toString()
                            ?: "--"
                    }°", fontSize = 14.sp
                )
            }
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

