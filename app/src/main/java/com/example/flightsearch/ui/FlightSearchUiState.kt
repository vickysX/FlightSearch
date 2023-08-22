package com.example.flightsearch.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Favorite

//sealed class FlightSearchUiState {

    data class Flight(
        val departureCode: String,
        val destinationCode: String,
        var isFavorite: Boolean
    )


/*    data class UiState(
        var userQuery: String = "",
        var airports: List<Airport> = listOf(),
        var flights: SnapshotStateList<Flight> = mutableStateListOf(),
        var favorites: List<Favorite> = listOf()
    )

}*/

fun Flight.toFavorite() : Favorite =
    Favorite(
        departureCode = this.departureCode,
        destinationCode = this.destinationCode
    )

fun Favorite.toFlight() : Flight =
    Flight(
        departureCode = this.departureCode,
        destinationCode = this.destinationCode,
        isFavorite = true
    )
