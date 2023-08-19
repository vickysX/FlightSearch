package com.example.flightsearch.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.models.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query("SELECT * FROM airport")
    fun getAllAirports() : Flow<List<Airport>>

    @Query(
        "SELECT * FROM airport WHERE iata_code LIKE :query OR name LIKE :query ORDER BY passengers LIMIT 8"
    )
    fun getSuggestedAirports(query: String) : Flow<List<Airport>>

}