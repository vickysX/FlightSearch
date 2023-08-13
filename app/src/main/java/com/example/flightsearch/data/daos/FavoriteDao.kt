package com.example.flightsearch.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.flightsearch.models.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT departure_code, destination_code FROM favorite")
    fun getFavoriteFlights() : Flow<List<Favorite>>

    @Insert
    suspend fun addFavoriteFlight(favorite: Favorite)

    @Delete
    suspend fun removeFavorite(favorite: Favorite)

}