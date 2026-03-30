package com.example.weatherapp.feature.city.presentation.model

data class CityUiItem(
    val id: String,
    val savedCityId: Long?,
    val title: String,
    val subtitle: String,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val isSelected: Boolean
)