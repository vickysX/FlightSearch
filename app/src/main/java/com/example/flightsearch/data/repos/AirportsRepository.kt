package com.example.flightsearch.data.repos

import com.example.flightsearch.models.Airport
import kotlinx.coroutines.flow.Flow

interface AirportsRepository {
    fun getAllAirports() : Flow<List<Airport>>
    fun getSuggestedAirports(query: String) : Flow<List<Airport>>
    fun getSelectedAirport(id: Int) : Flow<Airport>
    fun getAirportByIataCode(iataCode: String) : Flow<Airport>
}