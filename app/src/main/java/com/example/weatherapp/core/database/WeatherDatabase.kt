package com.example.weatherapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.core.database.dao.CityDao
import com.example.weatherapp.core.database.dao.WeatherDao
import com.example.weatherapp.core.database.entity.SavedCityEntity
import com.example.weatherapp.core.database.entity.WeatherCacheEntity

@Database(
    entities = [SavedCityEntity::class, WeatherCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun weatherDao(): WeatherDao
}