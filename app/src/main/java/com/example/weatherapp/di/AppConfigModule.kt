package com.example.weatherapp.di

import com.example.weatherapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Named("open_weather_api_key")
    fun provideOpenWeatherApiKey(): String = BuildConfig.OPEN_WEATHER_API_KEY
}
