package com.example.weatherapp.feature.city.domain.usecase

import com.example.weatherapp.feature.city.domain.model.City
import com.example.weatherapp.feature.city.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedCitiesUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    operator fun invoke(): Flow<List<City>> = cityRepository.observeSavedCities()
}