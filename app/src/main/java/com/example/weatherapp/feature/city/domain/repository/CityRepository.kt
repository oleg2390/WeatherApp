package com.example.weatherapp.feature.city.domain.repository

import com.example.weatherapp.core.common.AppResult
import com.example.weatherapp.feature.city.domain.model.City
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun observeSavedCities(): Flow<List<City>>
    suspend fun searchCities(query: String): AppResult<List<City>>
    suspend fun saveSelectedCity(city: City)
    suspend fun getSelectedCity(): City?
}