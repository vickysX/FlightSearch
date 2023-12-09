package com.example.flightsearch.ui

import android.util.Log
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class FlightSearchViewModel @Inject constructor(
    private val airportsRepository: AirportsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val userPreferencesRepository: PreferencesRepository
) : ViewModel() {

    var userInput by mutableStateOf("")
        private set

    private var _currentAirport: MutableStateFlow<Airport?> = MutableStateFlow(null)
    val currentAirport: StateFlow<Airport?> = _currentAirport.asStateFlow()

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
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    private var _airports : StateFlow<List<Airport>> =
        airportsRepository.getAllAirports().stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    private var favorites : StateFlow<List<Favorite>> =
        favoritesRepository.getFavoriteFlights().stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    private var _flights: MutableStateFlow<List<Flight>> = MutableStateFlow(emptyList())
    val flights: StateFlow<List<Flight>> = _flights.asStateFlow()

    private var _favoriteFlights: MutableStateFlow<MutableList<Flight>> =
        MutableStateFlow(mutableListOf())
    val favoriteFlights: StateFlow<List<Flight>> = _favoriteFlights.asStateFlow()

    fun updateQueryString(query: String) {
        userInput = query
    }

    fun saveQueryPreference() {
        viewModelScope.launch {
            userPreferencesRepository.saveQueryString(userInput)
        }
    }

    private fun loadPreferenceFromDataStore() : String {
        var queryPreference = ""
        viewModelScope.launch {
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
        _currentAirport.update { selectedAirport }
        viewModelScope.launch {
            val flightsList = mutableListOf<Flight>()
            var airportsList: List<Airport>
            airportsRepository.getAllAirports().collect {
                Log.d("ViewModel_Airports_Flow", it.toString())
                airportsList = it
                for (airport in airportsList) {
                    if (airport == selectedAirport) {
                        continue
                    }
                    flightsList.add(
                        Flight(
                            departureAirport = selectedAirport,
                            destinationAirport = airport,
                            isFavorite = false
                        )
                    )
                    val favList = favorites.value
                    if (favList.contains(flightsList.last().toFavorite())) {
                        flightsList.last().isFavorite = true
                    }
                }
                _flights.value = flightsList
                Log.d("ViewModel_Airports", airportsList.toString())
                Log.d("ViewModel", flightsList.toString())
                Log.d("ViewModel", _flights.value.toString())
            }
        }
    }

    private fun provideFavoritesAsFlights() {
        viewModelScope.launch {
            var favoritesList: List<Favorite>
            favoritesRepository.getFavoriteFlights().collect {
                favoritesList = it
                favoritesList.forEach {favorite ->
                    _favoriteFlights.value.add(
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
                Log.d("ViewModel_Favorites", favoritesList.toString())
            }
            /*favorites.value.map { favorite ->

            }*/
        }
    }

    init {
        userInput = loadPreferenceFromDataStore()
        provideFavoritesAsFlights()
    }

}