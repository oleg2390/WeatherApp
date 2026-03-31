package com.example.weatherapp.di

import com.example.weatherapp.feature.city.data.CityRepositoryImpl
import com.example.weatherapp.feature.city.domain.repository.CityRepository
import com.example.weatherapp.feature.home.data.WeatherRepositoryImpl
import com.example.weatherapp.feature.home.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCityRepository(
        impl: CityRepositoryImpl
    ): CityRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository
}