package com.example.flightsearch.data.repos

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val queryString: Flow<String>
    suspend fun saveQueryString(queryString: String)
}