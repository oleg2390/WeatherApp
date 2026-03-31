package com.example.weatherapp.feature.city.data

import com.example.weatherapp.core.common.AppResult
import com.example.weatherapp.core.database.dao.CityDao
import com.example.weatherapp.core.database.entity.SavedCityEntity
import com.example.weatherapp.core.network.api.OpenWeatherApi
import com.example.weatherapp.feature.city.data.mapper.toDomainCity
import com.example.weatherapp.feature.city.domain.model.City
import com.example.weatherapp.feature.city.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val api: OpenWeatherApi,
    private val cityDao: CityDao,
    @Named("open_weather_api_key") private val apiKey: String
) : CityRepository {
    override fun observeSavedCities(): Flow<List<City>> {
        return cityDao.observeSavedCities()
            .map { list-> list.map { it.toDomain() } }
    }

    override suspend fun searchCities(query: String): AppResult<List<City>> {
        return try {
            val dto = api.searchCities(
                query = query,
                limit = 5,
                appKey = apiKey
            )
            AppResult.Success(dto.map { it.toDomainCity() })
        } catch (t: Throwable) {
            AppResult.Error(t)
        }
    }

    override suspend fun saveSelectedCity(city: City) {
        val insertId = cityDao.insertIgnore(city.toEntity())
        val cityId = if (insertId == -1L) {
            cityDao.findByUnique(
                name = city.name,
                country = city.country,
                lat = city.lat,
                lon = city.lon
            )?.id ?: return
        }else {
            insertId
        }

        cityDao.clearSelected()
        cityDao.setSelected(cityId)
    }

    override suspend fun getSelectedCity(): City? {
        return cityDao.getSelectedCity()?.toDomain()
    }

    private fun SavedCityEntity.toDomain(): City = City(
        id = id,
        name = name,
        country = country,
        lat = lat,
        lon = lon,
        isSelected = isSelected
    )

    private fun City.toEntity(): SavedCityEntity = SavedCityEntity(
        id = id ?: 0L,
        name = name,
        country = country,
        lat = lat,
        lon = lon,
        isSelected = isSelected
    )
}