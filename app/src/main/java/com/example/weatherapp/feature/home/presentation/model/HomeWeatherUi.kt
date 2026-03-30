package com.example.weatherapp.feature.home.presentation.model

data class HomeWeatherUi(
    val current: CurrentWeatherUi,
    val hourly: List<HourlyWeatherUi>,
    val daily: List<DailyWeatherUi>
)

data class CurrentWeatherUi(
    val temp: String,
    val feelsLike: String,
    val humidityPercent: Int,
    val windSpeed: String,
    val description: String,
    val iconUrl: String
)

data class HourlyWeatherUi(
    val id: String,
    val time: String,
    val temp: String,
    val description: String,
    val iconUrl: String
)

data class DailyWeatherUi(
    val id: String,
    val day: String,
    val minTemp: String,
    val maxTemp: String,
    val description: String,
    val iconUrl: String
)
