package com.example.flightsearch.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalAirport
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flightsearch.R
import com.example.flightsearch.models.Airport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = hiltViewModel()
) {
    val userQuery = viewModel.userInput.collectAsStateWithLifecycle()
    val suggestions by viewModel.searchResults.collectAsStateWithLifecycle()
    val flights = viewModel.flights
    val favoriteFlights = viewModel.favoriteFlights
    val addToFav: (Flight) -> Unit = { viewModel.addToFavorites(it) }
    val removeFromFav: (Flight) -> Unit = {
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
    ) { paddingValues ->
        AppBody(
            modifier = Modifier.padding(paddingValues),
            query = userQuery.value,
            updateQuery = { viewModel.updateQueryString(it) },
            onInputSubmitted = { viewModel.saveQueryPreference() },
            suggestions = suggestions,
            onAirportClicked = {},
            flights = flights,
            favoriteFlights = favoriteFlights,
            addToFav = addToFav,
            removeFromFav = removeFromFav
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AppBody(
    modifier: Modifier = Modifier,
    query: String,
    updateQuery: (String) -> Unit,
    onInputSubmitted: () -> Unit,
    suggestions: List<Airport>,
    onAirportClicked: (Airport) -> Unit,
    flights: List<Flight>,
    favoriteFlights: List<Flight>,
    addToFav: (Flight) -> Unit,
    removeFromFav: (Flight) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var active by rememberSaveable {
            mutableStateOf(false)
        }
        Column(
            //horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            AirportsSearchBar(
                query = query,
                updateQuery = updateQuery,
                onInputSubmitted = onInputSubmitted,
                onAirportClicked = onAirportClicked,
                suggestions = suggestions,
                active = active,
                onActiveChange = {active = it}
            )
            if (!active) {
                Flights(
                    flights = favoriteFlights,
                    addToFav = addToFav,
                    removeFromFav = removeFromFav
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AirportsSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    updateQuery: (String) -> Unit,
    onInputSubmitted: () -> Unit,
    onAirportClicked: (Airport) -> Unit,
    suggestions: List<Airport>,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    SearchBar(
        query = query,
        onQueryChange =  {
            updateQuery(it)
            onInputSubmitted()
        },
        onSearch = {
            keyboardController?.hide()
            onInputSubmitted()
        },
        active = active,
        onActiveChange = onActiveChange,
        placeholder = {
            Text(text = stringResource(id = R.string.search_bar_placeholder))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(
                    id = R.string.search_bar_icon
                )
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { updateQuery("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(
                            id = R.string.clear_input
                        )
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        content = {
            Suggestions(
                airports = suggestions,
                onAirportClicked = onAirportClicked
            )
        }
    )
}

@Composable
fun Suggestions(
    modifier: Modifier = Modifier,
    airports: List<Airport>,
    onAirportClicked: (Airport) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(airports) {
            AirportItem(
                airport = it,
                onItemClicked = onAirportClicked
            )
        }
    }
}

@Composable
fun AirportItem(
    modifier: Modifier = Modifier,
    airport: Airport,
    onItemClicked: (Airport) -> Unit
) {
    Row(
        modifier = modifier.clickable {
            onItemClicked(airport)
        }
    ) {
        Icon(
            imageVector = Icons.Default.LocalAirport,
            contentDescription = stringResource(
                id = R.string.airport_item
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        AirportSpannable(airport = airport)
    }
}

@Composable
fun Flights(
    modifier: Modifier = Modifier,
    flights: List<Flight>,
    addToFav: (Flight) -> Unit,
    removeFromFav: (Flight) -> Unit
) {
    Column {
        Text(
            text = stringResource(
                id = R.string.favorite_flights
            )
        )
        LazyColumn {
            items(flights) { flight ->
                FlightCard(
                    flight = flight,
                    addToFav = addToFav,
                    removeFromFav = removeFromFav
                )
            }
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
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 4.dp,
                    bottom = 4.dp,
                    start = 4.dp,
                    end = 8.dp
                )
            ) {
                FlightCardItem(
                    resDepArr = R.string.departure,
                    airport = flight.departureAirport
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlightCardItem(
                    resDepArr = R.string.destination,
                    airport = flight.destinationAirport
                )
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
                    tint = if (flight.isFavorite) {
                        Color.Yellow
                    } else {
                        Color.Unspecified
                    },
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun FlightCardItem(
    modifier: Modifier = Modifier,
    @StringRes resDepArr: Int,
    airport: Airport
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        Text(
            text = stringResource(id = resDepArr)
        )
        AirportSpannable(airport = airport)
    }
}

@Composable
fun AirportSpannable(
    modifier: Modifier = Modifier,
    airport: Airport
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
    Text(
        text = airportName,
        //textAlign = TextAlign.Justify
    )
}

/*@Composable
@Preview
fun SearchBarPreview() {
    FlightSearchTheme {
        SearchBar(
            value = "",
            onValueChange = {},
            onInputSubmitted = {}
        )
    }
}*/

//@Composable
/*@Preview
fun AirportItemPreview() {
    FlightSearchTheme(
        darkTheme = true
    ) {
        AirportItem(
            airport = Airport(
                id = 0,
                iataCode = "FCO",
                name = "Leonardo Da Vinci International Airport",
                passengers = 1_000_000
            )
        )
    }
}*/

/*
@Composable
@Preview
fun FlightCard() {
    val departureAirport = Airport(
        id = 0,
        iataCode = "FCO",
        name = "Leonardo Da Vinci International Airport",
        passengers = 1_000_000
    )
    val destinationAirport = Airport(
        id = 1,
        iataCode = "DUB",
        name = "Dubin Airport",
        passengers = 700_000
    )
    FlightSearchTheme {
        FlightCard(
            flight = Flight(
                departureAirport = departureAirport,
                destinationAirport = destinationAirport,
                isFavorite = true
            ),
            addToFav = {},
            removeFromFav = {}
        )
    }
}*/
