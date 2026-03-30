package com.example.weatherapp.feature.home.domain.usecase

import com.example.weatherapp.core.common.AppResult
import com.example.weatherapp.feature.home.domain.model.WeatherBundle
import com.example.weatherapp.feature.home.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherForCityUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(
        cityId: Long?,
        lat: Double,
        lon: Double
    ): AppResult<WeatherBundle> {
        return when (val network = weatherRepository.getWeather(lat, lon)) {
            is AppResult.Success -> {
                if (cityId != null) {
                    weatherRepository.saveWeatherCache(cityId, network.data)
                }
                network
            }

            is AppResult.Error -> {
                val cached = cityId?.let { weatherRepository.getCachedWeather(it) }
                if (cached != null) {
                    AppResult.Success(cached)
                } else {
                    network
                }
            }
        }
    }
}