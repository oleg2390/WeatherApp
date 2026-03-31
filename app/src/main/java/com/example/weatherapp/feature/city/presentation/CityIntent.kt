package com.example.weatherapp.feature.city.presentation

import com.example.weatherapp.feature.city.presentation.model.CityUiItem

sealed interface CityIntent {
    data object OnBackClick : CityIntent
    data class OnQueryChanged(val query: String) : CityIntent
    data object OnSearchClick : CityIntent
    data class OnCityClick(val city: CityUiItem) : CityIntent
}