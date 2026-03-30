package com.example.weatherapp.core.location

data class GeoPoint(
    val lat: Double,
    val lon: Double
)

interface LocationRepository {
    suspend fun getLastKnownLocation(): GeoPoint?
}
