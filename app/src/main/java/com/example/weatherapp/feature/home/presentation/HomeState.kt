package com.example.weatherapp.feature.home.presentation

import com.example.weatherapp.R
import com.example.weatherapp.core.common.UiText
import com.example.weatherapp.feature.home.presentation.model.HomeWeatherUi

data class HomeState(
    val title: UiText = UiText.StringResource(R.string.home_title),
    val contentState: HomeContentState = HomeContentState.Loading,
    val showLocationAction: Boolean = false
)

sealed interface HomeContentState {
    data object Loading : HomeContentState
    data class Content(val weather: HomeWeatherUi) : HomeContentState
    data class Empty(val message: UiText) : HomeContentState
    data class Error(val message: UiText) : HomeContentState
}