package com.example.weatherapp.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.core.database.WeatherDatabase
import com.example.weatherapp.core.database.dao.CityDao
import com.example.weatherapp.core.database.dao.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "weather.db"

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCityDao(db: WeatherDatabase): CityDao = db.cityDao()

    @Provides
    @Singleton
    fun provideWeatherDao(db: WeatherDatabase): WeatherDao = db.weatherDao()
}