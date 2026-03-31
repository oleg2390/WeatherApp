package com.example.weatherapp.feature.home.presentation

sealed interface HomeIntent {
    data object OnOpenClick : HomeIntent
    data object OnRefresh : HomeIntent
    data object OnRequestLocationPermission : HomeIntent
    data class OnLocationPermissionResult(val granted: Boolean) : HomeIntent
}