package com.example.weatherapp.feature.city.presentation

sealed interface CityEffect {
    data object NavigateBack: CityEffect
}