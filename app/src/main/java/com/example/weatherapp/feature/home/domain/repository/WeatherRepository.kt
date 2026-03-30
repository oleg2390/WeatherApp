package com.example.weatherapp.feature.home.domain.repository

import com.example.weatherapp.core.common.AppResult
import com.example.weatherapp.feature.home.domain.model.WeatherBundle

interface WeatherRepository {
    suspend fun getWeather(lat: Double, lon: Double): AppResult<WeatherBundle>
    suspend fun getCachedWeather(cityId: Long): WeatherBundle?
    suspend fun saveWeatherCache(cityId: Long, data: WeatherBundle)
}