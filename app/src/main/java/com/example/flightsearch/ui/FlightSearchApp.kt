package com.example.flightsearch.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flightsearch.R
import com.example.flightsearch.ui.theme.FlightSearchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = hiltViewModel()
) {
    // WTF!!!???
    val userQuery = viewModel.queryString.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name)
                    )
                }
            )
        }
    ) {
        AppBody(
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun AppBody(
    modifier: Modifier = Modifier
) {
    TODO("Not yet implemented")
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(
                    id = R.string.search_bar_placeholder
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(
                    id = R.string.search_bar_icon
                )
            )
        }
    )
}

@Composable
fun FlightCard(
    modifier: Modifier = Modifier,
    flight: Flight,
    addToFav: (Flight) -> Unit,
    removeFromFav: (Flight) -> Unit
) {
    //var starColor =
    Card {
        Row {
            Column {

            }
            IconButton(
                onClick = {
                    !flight.isFavorite
                    when {
                        flight.isFavorite -> removeFromFav(flight)
                        else -> addToFav(flight)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
@Preview
fun SearchBarPreview() {
    FlightSearchTheme {
        SearchBar(value = "", onValueChange = {})
    }
}