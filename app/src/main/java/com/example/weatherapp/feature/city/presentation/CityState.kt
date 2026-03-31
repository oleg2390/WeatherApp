package com.example.weatherapp.feature.city.presentation

import com.example.weatherapp.core.common.UiText
import com.example.weatherapp.feature.city.presentation.model.CityUiItem
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class CityState(
    val query: String = "",
    val savedCities: PersistentList<CityUiItem> = persistentListOf(),
    val searchState: CitySearchState = CitySearchState.Idle,
)

sealed interface CitySearchState {
    data object Idle : CitySearchState
    data object Loading : CitySearchState
    data object Empty : CitySearchState
    data class Content(val cities: PersistentList<CityUiItem>) : CitySearchState
    data class Error(val message: UiText) : CitySearchState
}