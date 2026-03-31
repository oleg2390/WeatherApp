package com.example.weatherapp.feature.city.domain.model

data class City(
    val id: Long? = null,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val isSelected: Boolean = false
)
