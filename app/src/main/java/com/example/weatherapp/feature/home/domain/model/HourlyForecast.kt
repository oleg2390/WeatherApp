package com.example.weatherapp.feature.home.domain.model

data class HourlyForecast(
    val epochSeconds: Long,
    val tempCelsius: Double,
    val description: String,
    val iconCode: String
)
