package com.example.weatherapp.feature.city.presentation

import com.example.weatherapp.core.common.UiText
import com.example.weatherapp.feature.city.presentation.model.CityUiItem

data class CityState(
    val query: String = "",
    val savedCities: List<CityUiItem> = emptyList(),
    val searchState: CitySearchState = CitySearchState.Idle,
)

sealed interface CitySearchState {
    data object Idle : CitySearchState
    data object Loading : CitySearchState
    data object Empty : CitySearchState
    data class Content(val cities: List<CityUiItem>) : CitySearchState
    data class Error(val message: UiText) : CitySearchState
}