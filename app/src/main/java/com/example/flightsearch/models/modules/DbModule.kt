package com.example.flightsearch.models.modules

import android.content.Context
import com.example.flightsearch.data.FlightSearchDatabase
import com.example.flightsearch.data.daos.AirportDao
import com.example.flightsearch.data.daos.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    @Provides
    fun airportDao(@ApplicationContext context: Context) : AirportDao =
        FlightSearchDatabase.getDatabase(context).airportDao()

    @Provides
    fun favoriteDao(@ApplicationContext context: Context) : FavoriteDao =
        FlightSearchDatabase.getDatabase(context).favoriteDao()
}