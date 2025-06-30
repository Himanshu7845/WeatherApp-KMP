import com.weather.weatherapp.data.local.WeatherEntity
import com.weather.weatherapp.data.model.WeatherResponse
import com.weather.weatherapp.domain.use_cases.GetWeatherUseCase
import com.weather.weatherapp.utils.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf




class FakeGetWeatherUseCase(
    private val remoteResult: Flow<RestClientResult<WeatherResponse?>> = flowOf(),
    private val localResult: Flow<WeatherEntity> = flowOf()
) : GetWeatherUseCase(
    weatherRepository = DummyWeatherRepository(remoteResult),
    weatherLocalDataRepository = DummyLocalRepository(localResult)
)

class DummyWeatherRepository(
    private val result: Flow<RestClientResult<WeatherResponse?>>
) : com.weather.weatherapp.domain.repository.WeatherRepository {
    override suspend fun getWeather(lat: Double, lon: Double): RestClientResult<WeatherResponse> {
        return result.first() as RestClientResult<WeatherResponse>
    }
}
class DummyLocalRepository(
    private val localResult: Flow<WeatherEntity>
) : com.weather.weatherapp.domain.repository.WeatherLocalDataRepository {
    var insertCalled = false
    var clearCalled = false

    override suspend fun insertGrowth(entity: WeatherEntity): Long {
        insertCalled = true
        return 1L
    }

    override suspend fun clearDataBase() {
        clearCalled = true
    }

    override suspend fun fetchWeather(): Flow<WeatherEntity> {
        return localResult
    }
}
