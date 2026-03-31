package com.example.weatherapp.feature.home.presentation

sealed interface HomeEffect {
    data object NavigateToCity : HomeEffect
    data object RequestLocationPermission : HomeEffect
}