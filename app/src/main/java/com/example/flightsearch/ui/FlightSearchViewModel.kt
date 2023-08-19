package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import com.example.flightsearch.data.repos.AirportsRepository
import com.example.flightsearch.data.repos.FavoritesRepository
import com.example.flightsearch.data.repos.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FlightSearchViewModel @Inject constructor(
    private val airportsRepository: AirportsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {


}