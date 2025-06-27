import com.weather.weatherapp.data.local.WeatherEntity
import com.weather.weatherapp.data.model.CurrentWeather
import com.weather.weatherapp.data.model.CurrentWeatherUnits
import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.ui.view_models.WeatherViewModel
import com.weather.weatherapp.utils.RestClientResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeUseCase: FakeGetWeatherUseCase
    private lateinit var viewModel: WeatherViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful weather fetch updates UI state`() = runTest {
        val mockResponse = WeatherResponse(
            currentWeather = CurrentWeather(
                time = "2025-06-27T08:15",
                interval = 900,
                temperature = 18.7,
                windspeed = 8.6,
                winddirection = 33,
                isDay = 0,
                weathercode = 3
            ),
            currentWeatherUnits =  CurrentWeatherUnits(
                time = "iso8601",
                interval = "seconds",
                temperature = "°C",
                windspeed = "km/h",
                winddirection = "°",
                isDay = "",
                weathercode = "wmo code"
            ),
            generationtimeMs = 0.0598430633544922,
            latitude =40.710335,
            longitude = -73.99309,
            timezone = "GMT",
            timezoneAbbreviation = "GMT",
            utcOffsetSeconds = 0,
        )

        val dummyRemote = flow {
            emit(RestClientResult.loading())
            emit(RestClientResult.success(mockResponse))
        }
        val localRepo = DummyLocalRepository(flowOf())
        val fakeUseCase = FakeGetWeatherUseCase(
            remoteResult = dummyRemote,
            localResult = flowOf()
        )

        val viewModel = WeatherViewModel(fakeUseCase)

        viewModel.getWeather(22.0, 88.0, "Test City")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(mockResponse, state.data)
        assertTrue(localRepo.insertCalled)
        assertTrue(localRepo.clearCalled)
    }

    @Test
    fun `no internet fallback fetches local data`() = runTest {
        val localData = listOf(
            WeatherEntity(
                id = 1,
                cityName = "Offline City",
                temperature = 25.4,
                weatherCode = 10,
                windSpeed = 160.2,
                windDirection = 10
            )
        )

        fakeUseCase = FakeGetWeatherUseCase(
            remoteResult = flowOf(RestClientResult.error("No internet connection")),
            localResult = flowOf(localData)
        )

        viewModel = WeatherViewModel(fakeUseCase)

        viewModel.getWeather(22.0, 88.0, "OfflineCity")
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(localData, state.dataFromLocal)
        assertEquals("", state.error)
    }

    @Test
    fun `unknown error updates error message`() = runTest {
        fakeUseCase = FakeGetWeatherUseCase(
            remoteResult = flowOf(RestClientResult.error("Internal Server Error"))
        )

        viewModel = WeatherViewModel(fakeUseCase)

        viewModel.getWeather(22.0, 88.0, "AnyCity")
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals("Internal Server Error", state.error)
    }
}
