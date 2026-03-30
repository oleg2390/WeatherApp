package com.example.weatherapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.core.database.entity.WeatherCacheEntity

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_cache WHERE cityId = :cityId LIMIT 1")
    suspend fun getByCityId(cityId: Long): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: WeatherCacheEntity)
}