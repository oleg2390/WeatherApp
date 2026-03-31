package com.example.weatherapp.feature.city.data.mapper

import com.example.weatherapp.core.network.dto.GeoCodingDto
import com.example.weatherapp.feature.city.domain.model.City
import java.util.Locale

fun GeoCodingDto.toDomainCity(): City = City(
    id = null,
    name = localNames?.get("ru")?.takeIf { it.isNotBlank() } ?: name,
    country = country.toCountryDisplayRu(),
    lat = lat,
    lon = lon,
    isSelected = false,
)

private fun String.toCountryDisplayRu(): String {
    return runCatching { Locale("", this).getDisplayCountry(Locale("ru")) }
        .getOrDefault(this)
        .takeIf { it.isNotBlank() }
        ?: this
}
