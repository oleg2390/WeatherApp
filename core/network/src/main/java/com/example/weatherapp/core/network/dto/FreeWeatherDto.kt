package com.example.weatherapp.core.network.dto

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponseDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("timezone") val timezoneOffsetSeconds: Int,
    @SerializedName("main") val main: MainBlockDto,
    @SerializedName("wind") val wind: WindBlockDto,
    @SerializedName("weather") val weather: List<WeatherConditionDto>,
    @SerializedName("name") val cityName: String? = null,
    @SerializedName("sys") val sys: CurrentSysDto? = null
)

data class CurrentSysDto(
    @SerializedName("country") val countryCode: String? = null
)

data class ForecastResponseDto(
    @SerializedName("list") val list: List<ForecastItemDto>,
    @SerializedName("city") val city: ForecastCityDto
)

data class ForecastCityDto(
    @SerializedName("timezone") val timezoneOffsetSeconds: Int
)

data class ForecastItemDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("main") val main: ForecastMainBlockDto,
    @SerializedName("weather") val weather: List<WeatherConditionDto>
)

data class MainBlockDto(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("humidity") val humidity: Int
)

data class WindBlockDto(
    @SerializedName("speed") val speed: Double
)

data class ForecastMainBlockDto(
    @SerializedName("temp") val temp: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double
)

data class WeatherConditionDto(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)
