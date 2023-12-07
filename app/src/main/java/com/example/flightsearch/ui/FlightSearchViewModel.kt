package com.example.flightsearch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.repos.AirportsRepository
import com.example.flightsearch.data.repos.FavoritesRepository
import com.example.flightsearch.data.repos.PreferencesRepository
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Favorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val userPreferencesRepository: PreferencesRepository
) : ViewModel() {

    private val coroutineScope = viewModelScope

    var userInput by mutableStateOf("")
        private set

    private val airportsFlow = airportsRepository.getAllAirports()

    val searchResults: StateFlow<List<Airport>> =
        snapshotFlow { userInput }
            .combine(airportsFlow) { query, airports ->
                when {
                    query.isNotEmpty() -> airports.filter { airport ->
                        airport.name.contains(query, ignoreCase = true) ||
                                airport.iataCode.contains(query, ignoreCase = true)
                    }
                    else -> airports
                }
            }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    private var airports : StateFlow<List<Airport>> =
        airportsFlow.map {
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

    var flights = mutableStateListOf<Flight>()
    var favoriteFlights = mutableStateListOf<Flight>()

    fun createListOfFlights(id: Int) {
        coroutineScope.launch {
            val selectedAirport = airportsRepository.getSelectedAirport(id)
            airports.first().map {

            }
        }
    }

    fun updateQueryString(query: String) {
        userInput = query
    }

    fun saveQueryPreference() {
        coroutineScope.launch {
            userPreferencesRepository.saveQueryString(userInput)
        }
    }

    private fun loadPreferenceFromDataStore() : String {
        var queryPreference = ""
        coroutineScope.launch {
            queryPreference = userPreferencesRepository.queryString.first()
        }
        return queryPreference
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

    fun provideFlights(selectedAirport: Airport) {
        for (airport in airports.value) {
            if (airport == selectedAirport) {
                continue
            }
            flights.add(
                Flight(
                    departureAirport = selectedAirport,
                    destinationAirport = airport,
                    isFavorite = false
                )
            )
            if (favorites.value.contains(flights.last().toFavorite())) {
                flights.last().isFavorite = true
            }
        }
    }

    fun provideFavoritesAsFlights() {
        coroutineScope.launch {
            favorites.value.map { favorite ->
                favoriteFlights.add(
                    Flight(
                        airportsRepository
                            .getAirportByIataCode(favorite.departureCode)
                            .first(),
                        airportsRepository
                            .getAirportByIataCode(favorite.destinationCode)
                            .first(),
                        true
                    )
                )
            }
        }
    }

    init {
        userInput = loadPreferenceFromDataStore()
    }

}