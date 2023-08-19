package com.example.flightsearch.data.repos

import com.example.flightsearch.data.daos.AirportDao
import com.example.flightsearch.models.Airport
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DbAirportsRepository @Inject constructor(
    private val airportDao: AirportDao
) : AirportsRepository {

    override fun getAllAirports(): Flow<List<Airport>> = airportDao.getAllAirports()

    override fun getSuggestedAirports(query: String): Flow<List<Airport>> =
        airportDao.getSuggestedAirports(query)

}