package com.example.flightsearch.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.models.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllAirports() : Flow<List<Airport>>

    @Query(
        "SELECT * FROM airport WHERE iata_code = :query OR name LIKE :query ORDER BY passengers DESC LIMIT 8"
    )
    fun getSuggestedAirports(query: String) : Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE id = :id")
    fun getSelectedAirport(id: Int) : Flow<Airport>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    fun getAirportByIataCode(iataCode: String) : Flow<Airport>

}