package com.example.weatherapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.core.database.entity.SavedCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM saved_cities ORDER BY isSelected DESC, name ASC")
    fun observeSavedCities(): Flow<List<SavedCityEntity>>

    @Query("SELECT * FROM saved_cities WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedCity(): SavedCityEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(city: SavedCityEntity): Long

    @Query("UPDATE saved_cities SET isSelected = 0")
    suspend fun clearSelected()

    @Query("UPDATE saved_cities SET isSelected = 1 WHERE id = :cityId")
    suspend fun setSelected(cityId: Long)

    @Query("SELECT * FROM saved_cities WHERE name = :name AND country = :country AND lat = :lat AND lon = :lon LIMIT 1")
    suspend fun findByUnique(name: String, country: String, lat: Double, lon: Double): SavedCityEntity?

}