package com.example.weatherapp.feature.home.data.mapper

import com.example.weatherapp.core.network.dto.CurrentWeatherResponseDto
import com.example.weatherapp.core.network.dto.ForecastItemDto
import com.example.weatherapp.core.network.dto.ForecastResponseDto
import com.example.weatherapp.feature.home.domain.model.CurrentWeather
import com.example.weatherapp.feature.home.domain.model.DailyForecast
import com.example.weatherapp.feature.home.domain.model.HourlyForecast
import com.example.weatherapp.feature.home.domain.model.WeatherBundle
import kotlin.math.max
import kotlin.math.min

private const val DEFAULT_DESCRIPTION = ""
private const val DEFAULT_ICON = ""
private const val DAY_SECONDS = 86_400L

fun toDomainWeatherBundle(
    current: CurrentWeatherResponseDto,
    forecast: ForecastResponseDto
): WeatherBundle {
    val timezoneOffset = current.timezoneOffsetSeconds.toLong()
    val hourly = forecast.list
        .take(24)
        .map { it.toDomainHourly() }
    val daily = forecast.list
        .groupBy { item -> (item.dt + timezoneOffset) / DAY_SECONDS }
        .values
        .take(7)
        .map { dayItems -> dayItems.toDomainDaily() }
    return WeatherBundle(
        current = current.toDomainCurrent(),
        daily = daily,
        hourly = hourly,
        timezone = timezoneOffset.toString(),
        updatedAtEpochSeconds = current.dt,
        cityName = current.cityName,
        countryCode = current.sys?.countryCode
    )
}

private fun CurrentWeatherResponseDto.toDomainCurrent(): CurrentWeather = CurrentWeather(
    tempCelsius = main.temp,
    feelsLikeCelsius = main.feelsLike,
    humidity = main.humidity,
    windSpeed = wind.speed,
    description = weather.firstOrNull()?.description ?: DEFAULT_DESCRIPTION,
    iconCode = weather.firstOrNull()?.icon ?: DEFAULT_ICON
)

private fun ForecastItemDto.toDomainHourly(): HourlyForecast = HourlyForecast(
    epochSeconds = dt,
    tempCelsius = main.temp,
    description = weather.firstOrNull()?.description ?: DEFAULT_DESCRIPTION,
    iconCode = weather.firstOrNull()?.icon ?: DEFAULT_ICON
)

private fun List<ForecastItemDto>.toDomainDaily(): DailyForecast {
    val first = first()
    var minTemp = Double.POSITIVE_INFINITY
    var maxTemp = Double.NEGATIVE_INFINITY
    for (item in this) {
        minTemp = min(minTemp, item.main.tempMin)
        maxTemp = max(maxTemp, item.main.tempMax)
    }
    return DailyForecast(
        epochSeconds = first.dt,
        minTempCelsius = minTemp,
        maxTempCelsius = maxTemp,
        description = first.weather.firstOrNull()?.description ?: DEFAULT_DESCRIPTION,
        iconCode = first.weather.firstOrNull()?.icon ?: DEFAULT_ICON
    )
}
