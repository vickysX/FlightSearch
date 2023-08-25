package com.example.flightsearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalAirport
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flightsearch.R
import com.example.flightsearch.models.Airport
import com.example.flightsearch.ui.theme.FlightSearchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = hiltViewModel()
) {
    val userQuery = viewModel.queryString.collectAsState()
    val suggestions = viewModel.suggestions.collectAsState()
    val flights = viewModel.flights
    val favoriteFlights = viewModel.favoriteFlights
    val addToFav : (Flight) -> Unit = {viewModel.addToFavorites(it)}
    val removeFromFav : (Flight) -> Unit = {
        viewModel.removeFromFavorites(it)
    }
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
    ) {paddingValues ->
        AppBody(
            modifier = Modifier.padding(paddingValues),
            query = userQuery.value,
            updateQuery = {viewModel.updateQueryString(it)},
            suggestions = suggestions.value,
            flights = flights,
            favoriteFlights = favoriteFlights,
            addToFav = addToFav,
            removeFromFav = removeFromFav
        )
    }
}

@Composable
fun AppBody(
    modifier: Modifier = Modifier,
    query: String,
    updateQuery: (String) -> Unit,
    suggestions: List<Airport>,
    flights: List<Flight>,
    favoriteFlights: List<Flight>,
    addToFav: (Flight) -> Unit,
    removeFromFav: (Flight) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        SearchBar(
            value = query,
            onValueChange = updateQuery
        )
        /** TODO: Conditional rendering of lists **/
        if (query.isEmpty()) {
            Flights(
                flights = favoriteFlights,
                addToFav = addToFav,
                removeFromFav = removeFromFav
            )
        } else {

        }
    }
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
        },
        shape = RoundedCornerShape(32.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun Suggestions(
    modifier: Modifier = Modifier,
    airports: List<Airport>
) {
    LazyColumn() {
        items(airports) {

        }
    }
}

@Composable
fun AirportItem(
    modifier: Modifier = Modifier,
    airport: Airport
) {
    Row (
        modifier = modifier.clickable {
            // TODO: Define function to make appear the list of flights
        }
    ) {
        val airportName = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(airport.iataCode)
            }
            append(" ")
            append(airport.name)
        }
        Icon(
            imageVector = Icons.Default.LocalAirport,
            contentDescription = stringResource(
                id = R.string.airport_item
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = airportName)
    }
}

@Composable
fun Flights(
    modifier: Modifier = Modifier,
    flights: List<Flight>,
    addToFav: (Flight) -> Unit,
    removeFromFav: (Flight) -> Unit
) {
    LazyColumn {
        items(flights) {flight ->
            FlightCard(
                flight = flight,
                addToFav = addToFav,
                removeFromFav = removeFromFav
            )
        }
    }
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

@Composable
@Preview
fun AirportItemPreview() {
    FlightSearchTheme {
        AirportItem(
            airport = Airport(
                id = 0,
                iataCode = "FCO",
                name = "Leonardo Da Vinci International Airport",
                passengers = 1_000_000
            )
        )
    }
}