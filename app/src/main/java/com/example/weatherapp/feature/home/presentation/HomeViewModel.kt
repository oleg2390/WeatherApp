package com.example.weatherapp.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.core.common.AppResult
import com.example.weatherapp.core.common.UiText
import com.example.weatherapp.core.location.usecase.GetCurrentLocationUseCase
import com.example.weatherapp.feature.city.domain.model.City
import com.example.weatherapp.feature.city.domain.usecase.GetSelectedCityUseCase
import com.example.weatherapp.feature.city.domain.usecase.SaveSelectedCityUseCase
import com.example.weatherapp.feature.home.domain.model.DailyForecast
import com.example.weatherapp.feature.home.domain.model.HourlyForecast
import com.example.weatherapp.feature.home.domain.model.WeatherBundle
import com.example.weatherapp.feature.home.domain.usecase.GetWeatherForCityUseCase
import com.example.weatherapp.feature.home.presentation.model.CurrentWeatherUi
import com.example.weatherapp.feature.home.presentation.model.DailyWeatherUi
import com.example.weatherapp.feature.home.presentation.model.HomeWeatherUi
import com.example.weatherapp.feature.home.presentation.model.HourlyWeatherUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSelectedCityUseCase: GetSelectedCityUseCase,
    private val saveSelectedCityUseCase: SaveSelectedCityUseCase,
    private val getWeatherForCityUseCase: GetWeatherForCityUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()

    private var locationPermissionDenied = false
    private var hasRequestedLocationPermission = false

    init {
        onIntent(HomeIntent.OnRefresh)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OnOpenClick -> {
                viewModelScope.launch {
                    _effect.emit(HomeEffect.NavigateToCity)
                }
            }

            HomeIntent.OnRefresh -> loadWeather()
            HomeIntent.OnRequestLocationPermission -> {
                viewModelScope.launch {
                    hasRequestedLocationPermission = true
                    _effect.emit(HomeEffect.RequestLocationPermission)
                }
            }

            is HomeIntent.OnLocationPermissionResult -> {
                if (intent.granted) {
                    locationPermissionDenied = false
                    hasRequestedLocationPermission = true
                    loadWeatherByCurrentLocation()
                } else {
                    locationPermissionDenied = true
                    _state.update {
                        it.copy(
                            contentState = HomeContentState.Empty(
                                UiText.StringResource(R.string.home_location_denied_message)
                            ),
                            showLocationAction = true
                        )
                    }
                }
            }
        }
    }

    private fun loadWeather() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    title = UiText.StringResource(R.string.home_title),
                    contentState = HomeContentState.Loading,
                    showLocationAction = false
                )
            }

            val city = getSelectedCityUseCase()

            if (city == null) {
                if (locationPermissionDenied) {
                    _state.update {
                        it.copy(
                            contentState = HomeContentState.Empty(
                                UiText.StringResource(R.string.home_location_denied_message)
                            ),
                            showLocationAction = true
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            contentState = HomeContentState.Empty(
                                UiText.StringResource(R.string.home_no_city_message)
                            ),
                            showLocationAction = true
                        )
                    }
                    if (!hasRequestedLocationPermission) {
                        hasRequestedLocationPermission = true
                        _effect.emit(HomeEffect.RequestLocationPermission)
                    }
                }
                return@launch
            }

            when (val result = getWeatherForCityUseCase(city.id, city.lat, city.lon)) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            title = UiText.DynamicString("${city.name}, ${city.country}"),
                            contentState = HomeContentState.Content(result.data.toHomeWeatherUi()),
                            showLocationAction = false
                        )
                    }
                }

                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            contentState = HomeContentState.Error(
                                UiText.StringResource(R.string.home_weather_load_error)
                            ),
                            showLocationAction = false
                        )
                    }
                }
            }
        }
    }

    private fun loadWeatherByCurrentLocation() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    title = UiText.StringResource(R.string.home_my_location_title),
                    contentState = HomeContentState.Loading,
                    showLocationAction = false
                )
            }

            val location = getCurrentLocationUseCase()
            if (location == null) {
                _state.update {
                    it.copy(
                        contentState = HomeContentState.Empty(
                            UiText.StringResource(R.string.home_location_not_found_message)
                        ),
                        showLocationAction = true
                    )
                }
                return@launch
            }

            when (val result = getWeatherForCityUseCase(null, location.lat, location.lon)) {
                is AppResult.Success -> {
                    val autoSelectedCity = result.data.toAutoSelectedCity(
                        lat = location.lat,
                        lon = location.lon
                    )
                    if (autoSelectedCity != null) {
                        saveSelectedCityUseCase(autoSelectedCity)
                    }
                    _state.update {
                        it.copy(
                            title = autoSelectedCity?.let { city ->
                                UiText.DynamicString("${city.name}, ${city.country}")
                            } ?: UiText.StringResource(R.string.home_my_location_title),
                            contentState = HomeContentState.Content(result.data.toHomeWeatherUi())
                        )
                    }
                }

                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            contentState = HomeContentState.Error(
                                UiText.StringResource(R.string.home_weather_load_error)
                            ),
                            showLocationAction = true
                        )
                    }
                }
            }
        }
    }

    private fun WeatherBundle.toHomeWeatherUi(): HomeWeatherUi {
        val timezoneOffsetSeconds = timezone.toIntOrNull() ?: 0
        return HomeWeatherUi(
            current = CurrentWeatherUi(
                temp = current.tempCelsius.toDisplayTemp(),
                feelsLike = current.feelsLikeCelsius.toDisplayTemp(),
                humidityPercent = current.humidity,
                windSpeed = current.windSpeed.toDisplaySpeed(),
                description = current.description.toSentenceCase(),
                iconUrl = buildIconUrl(current.iconCode)
            ),
            hourly = hourly.take(12).map { it.toHourlyUi(timezoneOffsetSeconds) }.toPersistentList(),
            daily = daily.take(5).map { it.toDailyUi(timezoneOffsetSeconds) }.toPersistentList()
        )
    }

    private fun HourlyForecast.toHourlyUi(timezoneOffsetSeconds: Int): HourlyWeatherUi {
        return HourlyWeatherUi(
            id = "hour_$epochSeconds",
            time = formatHour(epochSeconds, timezoneOffsetSeconds),
            temp = tempCelsius.toDisplayTemp(),
            description = description.toSentenceCase(),
            iconUrl = buildIconUrl(iconCode)
        )
    }

    private fun DailyForecast.toDailyUi(timezoneOffsetSeconds: Int): DailyWeatherUi {
        return DailyWeatherUi(
            id = "day_$epochSeconds",
            day = formatDay(epochSeconds, timezoneOffsetSeconds),
            minTemp = minTempCelsius.toDisplayTemp(),
            maxTemp = maxTempCelsius.toDisplayTemp(),
            description = description.toSentenceCase(),
            iconUrl = buildIconUrl(iconCode)
        )
    }

    private fun Double.toDisplayTemp(): String = "${roundToInt()}°C"

    private fun Double.toDisplaySpeed(): String = "${"%.1f".format(Locale.US, this)} м/с"

    private fun String.toSentenceCase(): String {
        if (isBlank()) return this
        return replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale("ru")) else it.toString()
        }
    }

    private fun formatHour(epochSeconds: Long, timezoneOffsetSeconds: Int): String {
        val format = SimpleDateFormat("HH:mm", Locale("ru"))
        format.timeZone = TimeZone.getTimeZone(offsetToGmtId(timezoneOffsetSeconds))
        return format.format(Date(epochSeconds * 1000))
    }

    private fun formatDay(epochSeconds: Long, timezoneOffsetSeconds: Int): String {
        val format = SimpleDateFormat("EEE, d MMM", Locale("ru"))
        format.timeZone = TimeZone.getTimeZone(offsetToGmtId(timezoneOffsetSeconds))
        return format.format(Date(epochSeconds * 1000))
    }

    private fun offsetToGmtId(offsetSeconds: Int): String {
        val sign = if (offsetSeconds >= 0) "+" else "-"
        val abs = kotlin.math.abs(offsetSeconds)
        val hours = abs / 3600
        val minutes = (abs % 3600) / 60
        return "GMT%s%02d:%02d".format(Locale.US, sign, hours, minutes)
    }

    private fun buildIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    }

    private fun WeatherBundle.toAutoSelectedCity(lat: Double, lon: Double): City? {
        val resolvedName = cityName?.takeIf { it.isNotBlank() } ?: return null
        val resolvedCountry = countryCode
            ?.toCountryDisplayRu()
            ?.takeIf { it.isNotBlank() }
            ?: return null
        return City(
            name = resolvedName,
            country = resolvedCountry,
            lat = lat,
            lon = lon,
            isSelected = true
        )
    }

    private fun String.toCountryDisplayRu(): String {
        return runCatching { Locale("", this).getDisplayCountry(Locale("ru")) }
            .getOrDefault(this)
            .takeIf { it.isNotBlank() }
            ?: this
    }
}