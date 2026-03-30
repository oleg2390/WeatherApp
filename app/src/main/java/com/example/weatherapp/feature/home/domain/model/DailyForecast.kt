package com.example.weatherapp.feature.home.domain.model

data class DailyForecast(
    val epochSeconds: Long,
    val minTempCelsius: Double,
    val maxTempCelsius: Double,
    val description: String,
    val iconCode: String
)
