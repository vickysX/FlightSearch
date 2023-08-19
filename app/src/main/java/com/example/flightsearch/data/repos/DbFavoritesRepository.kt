package com.example.flightsearch.data.repos

import com.example.flightsearch.data.daos.FavoriteDao
import com.example.flightsearch.models.Favorite
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DbFavoritesRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoritesRepository {

    override fun getFavoriteFlights(): Flow<List<Favorite>> =
        favoriteDao.getFavoriteFlights()

    override suspend fun addFavoriteFlight(favorite: Favorite) =
        favoriteDao.addFavoriteFlight(favorite)

    override suspend fun removeFavorite(favorite: Favorite) =
        favoriteDao.removeFavorite(favorite)

}