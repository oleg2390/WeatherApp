package com.example.weatherapp.feature.home.presentation.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList

@Immutable
data class HomeWeatherUi(
    val current: CurrentWeatherUi,
    val hourly: PersistentList<HourlyWeatherUi>,
    val daily: PersistentList<DailyWeatherUi>

)

@Immutable
data class CurrentWeatherUi(
    val temp: String,
    val feelsLike: String,
    val humidityPercent: Int,
    val windSpeed: String,
    val description: String,
    val iconUrl: String
)

@Immutable
data class HourlyWeatherUi(
    val id: String,
    val time: String,
    val temp: String,
    val description: String,
    val iconUrl: String
)

@Immutable
data class DailyWeatherUi(
    val id: String,
    val day: String,
    val minTemp: String,
    val maxTemp: String,
    val description: String,
    val iconUrl: String
)
