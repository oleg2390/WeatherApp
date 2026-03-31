package com.example.weatherapp.feature.city.domain.usecase

import com.example.weatherapp.core.common.AppResult
import com.example.weatherapp.feature.city.domain.model.City
import com.example.weatherapp.feature.city.domain.repository.CityRepository
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(query: String): AppResult<List<City>> {
        if (query.isBlank()) return AppResult.Success(emptyList())
        return cityRepository.searchCities(query.trim())
    }
}