package com.example.flightsearch.data.repos

import com.example.flightsearch.models.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoriteFlights() : Flow<List<Favorite>>
    suspend fun addFavoriteFlight(favorite: Favorite)
    suspend fun removeFavorite(favorite: Favorite)
}