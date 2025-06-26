package com.weather.weatherapp.android

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.landscapist.glide.GlideImage
import com.weather.weatherapp.ui.permission.AndroidLocationService
import com.weather.weatherapp.ui.view_models.UiState
import com.weather.weatherapp.ui.view_models.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = koinViewModel<WeatherViewModel>()
                    var locationService by remember { mutableStateOf<AndroidLocationService?>(null) }
                    val scope = rememberCoroutineScope()

                    val permission =
                        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { bool ->
                            if (bool) {
                                scope.launch(Dispatchers.IO) {
                                    val location = locationService?.getLocation()
                                    location?.run {
                                        viewModel.getWeather(latitude, longitude)
                                    }
                                }
                            }
                        }

                    val context = LocalContext.current
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(key1 = Unit) {
                        permission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        locationService = AndroidLocationService(context, permission)
                        locationService?.requestLocationPermission { bool -> }
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
