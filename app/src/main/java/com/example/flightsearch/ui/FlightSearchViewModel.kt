package com.example.flightsearch.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.repos.AirportsRepository
import com.example.flightsearch.data.repos.FavoritesRepository
import com.example.flightsearch.data.repos.UserPreferencesRepository
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Favorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L


@HiltViewModel
class FlightSearchViewModel @Inject constructor(
    private val airportsRepository: AirportsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val coroutineScope = viewModelScope

    var queryString : StateFlow<String> =
        userPreferencesRepository.queryString.map {
            it
        }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = ""
            )

    private var airports : StateFlow<List<Airport>> =
        airportsRepository.getAllAirports().map {
            it
        }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = listOf()
            )

    private var favorites : StateFlow<List<Favorite>> =
        favoritesRepository.getFavoriteFlights().map {
            it
        }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = listOf()
            )

    var suggestions : StateFlow<List<Airport>> =
        airportsRepository.getSuggestedAirports(queryString.value).map {
            it
        }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = listOf()
            )

    var flights = mutableStateListOf<Flight>()
    var favoriteFlights = mutableStateListOf<Flight>()

    fun createListOfFlights(id: Int) {
        viewModelScope.launch {
            val selectedAirport = airportsRepository.getSelectedAirport(id)
            airports.first().map {

            }
        }
    }

    fun updateQueryString(query: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveQueryString(query)
        }
    }

    fun addToFavorites(flight: Flight) {
        viewModelScope.launch {
            favoritesRepository.addFavoriteFlight(flight.toFavorite())
        }
    }

    fun removeFromFavorites(flight: Flight) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(flight.toFavorite())
        }
    }

    fun provideFlights(airport: Airport) {
        airports.value.map {
            flights.add(
                Flight(
                    departureCode = airport.iataCode,
                    destinationCode = it.iataCode,
                    isFavorite = false
                )
            )
            if (favorites.value.contains(flights.last().toFavorite())) {
                flights.last().isFavorite = true
            }
        }
    }

    fun provideFavoritesAsFlights() {
        favorites.value.map { favorite ->
            favoriteFlights.add(
                favorite.toFlight()
            )
        }
    }

}