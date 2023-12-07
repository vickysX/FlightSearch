package com.example.flightsearch.data.repos

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


class UserPreferencesRepository @Inject constructor(
    private val dataStore : DataStore<Preferences>
) : PreferencesRepository {

    private companion object {
        val QUERY_STRING = stringPreferencesKey("query_string")
        const val TAG = "UserPreferencesRepo"
    }

    override val queryString : Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[QUERY_STRING] ?: ""
        }

    override suspend fun saveQueryString(queryString: String) {
        dataStore.edit { preferences ->
            preferences[QUERY_STRING] = queryString
        }
    }

}