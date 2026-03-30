package com.example.weatherapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val cityId: Long,
    val payloadJson: String,
    val updatedAtEpochSeconds: Long,
)
