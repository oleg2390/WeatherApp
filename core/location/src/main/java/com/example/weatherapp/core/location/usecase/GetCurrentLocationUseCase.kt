package com.example.weatherapp.core.location.usecase

import com.example.weatherapp.core.location.GeoPoint
import com.example.weatherapp.core.location.LocationRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): GeoPoint? = locationRepository.getLastKnownLocation()
}
