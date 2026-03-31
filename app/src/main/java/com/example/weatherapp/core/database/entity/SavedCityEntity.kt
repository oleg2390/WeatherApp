package com.example.weatherapp.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_cities",
    indices = [Index(value = ["name", "country", "lat", "lon"], unique = true)]
)
data class SavedCityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val isSelected: Boolean = false,
)
