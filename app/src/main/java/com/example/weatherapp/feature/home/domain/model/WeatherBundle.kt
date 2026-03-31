package com.example.weatherapp.feature.home.domain.model

data class WeatherBundle(
    val current: CurrentWeather,
    val daily: List<DailyForecast>,
    val hourly: List<HourlyForecast>,
    val timezone: String,
    val updatedAtEpochSeconds: Long,
    val cityName: String? = null,
    val countryCode: String? = null
)
