package com.example.flightsearch.models.modules

import com.example.flightsearch.data.repos.AirportsRepository
import com.example.flightsearch.data.repos.DbAirportsRepository
import com.example.flightsearch.data.repos.DbFavoritesRepository
import com.example.flightsearch.data.repos.FavoritesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ReposModule {

    @Binds
    abstract fun provideAirportsRepository(
        dbAirportsRepository: DbAirportsRepository
    ) : AirportsRepository

    @Binds
    abstract fun provideFavoritesRepository(
        dbFavoritesRepository: DbFavoritesRepository
    ) : FavoritesRepository

}