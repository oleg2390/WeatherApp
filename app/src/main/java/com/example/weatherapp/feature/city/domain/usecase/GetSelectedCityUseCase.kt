package com.example.weatherapp.feature.city.domain.usecase

import com.example.weatherapp.feature.city.domain.model.City
import com.example.weatherapp.feature.city.domain.repository.CityRepository
import javax.inject.Inject

class GetSelectedCityUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(): City? = cityRepository.getSelectedCity()
}