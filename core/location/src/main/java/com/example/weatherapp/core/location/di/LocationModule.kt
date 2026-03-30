package com.example.weatherapp.core.location.di

import com.example.weatherapp.core.location.LocationRepository
import com.example.weatherapp.core.location.LocationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        imp: LocationRepositoryImpl
    ): LocationRepository
}
