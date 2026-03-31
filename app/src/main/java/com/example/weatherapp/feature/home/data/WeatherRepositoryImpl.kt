package com.example.weatherapp.feature.home.data

import com.example.weatherapp.core.common.AppResult
import com.example.weatherapp.core.database.dao.WeatherDao
import com.example.weatherapp.core.database.entity.WeatherCacheEntity
import com.example.weatherapp.core.network.api.OpenWeatherApi
import com.example.weatherapp.feature.home.data.mapper.toDomainWeatherBundle
import com.example.weatherapp.feature.home.domain.model.WeatherBundle
import com.example.weatherapp.feature.home.domain.repository.WeatherRepository
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val weatherDao: WeatherDao,
    private val gson: Gson,
    @Named("open_weather_api_key") private val apiKey: String
) : WeatherRepository {

    override suspend fun getWeather(
        lat: Double,
        lon: Double
    ): AppResult<WeatherBundle> {
        return try {
            val currentDto = api.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = apiKey
            )
            val forecastDto = api.getForecast(
                lat = lat,
                lon = lon,
                apiKey = apiKey
            )
            AppResult.Success(
                toDomainWeatherBundle(
                    current = currentDto,
                    forecast = forecastDto
                )
            )
        } catch (t: Throwable) {
            AppResult.Error(t)
        }
    }

    override suspend fun getCachedWeather(cityId: Long): WeatherBundle? {
        val cache = weatherDao.getByCityId(cityId) ?: return null
        return runCatching {
            gson.fromJson(cache.payloadJson, WeatherBundle::class.java)
        }.getOrNull()
    }

    override suspend fun saveWeatherCache(
        cityId: Long,
        data: WeatherBundle
    ) {
        val payload = gson.toJson(data)
        weatherDao.upsert(
            WeatherCacheEntity(
                cityId = cityId,
                payloadJson = payload,
                updatedAtEpochSeconds = data.updatedAtEpochSeconds
            )
        )
    }
}