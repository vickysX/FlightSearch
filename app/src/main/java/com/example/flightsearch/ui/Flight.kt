package com.example.flightsearch.ui

import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Favorite

data class Flight(
    val departureAirport: Airport,
    val destinationAirport: Airport,
    var isFavorite: Boolean
)

fun Flight.toFavorite() : Favorite =
    Favorite(
        departureCode = this.departureAirport.iataCode,
        destinationCode = this.destinationAirport.iataCode
    )
