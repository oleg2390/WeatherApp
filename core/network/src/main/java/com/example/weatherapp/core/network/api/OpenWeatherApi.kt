package com.example.weatherapp.core.network.api

import com.example.weatherapp.core.network.dto.CurrentWeatherResponseDto
import com.example.weatherapp.core.network.dto.ForecastResponseDto
import com.example.weatherapp.core.network.dto.GeoCodingDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @GET("geo/1.0/direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") appKey: String
    ): List<GeoCodingDto>

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru",
        @Query("appid") apiKey: String
    ): CurrentWeatherResponseDto

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru",
        @Query("appid") apiKey: String
    ): ForecastResponseDto
}
