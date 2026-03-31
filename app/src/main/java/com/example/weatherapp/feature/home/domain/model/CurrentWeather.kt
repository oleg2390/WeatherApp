package com.example.weatherapp.feature.home.domain.model

data class CurrentWeather(
    val tempCelsius: Double,
    val feelsLikeCelsius: Double,
    val humidity: Int,
    val windSpeed: Double,
    val description: String,
    val iconCode: String
)
