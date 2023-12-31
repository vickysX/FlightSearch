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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalAirport
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flightsearch.R
import com.example.flightsearch.models.Airport
import com.example.flightsearch.ui.theme.FlightSearchTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = hiltViewModel()
) {
    val userQuery = viewModel.userInput
    val suggestions by viewModel.searchResults.collectAsStateWithLifecycle()
    val flights by viewModel.flights.collectAsStateWithLifecycle()
    val favoriteFlights by viewModel.favoriteFlights.collectAsStateWithLifecycle()
    val addToFav: (Flight) -> Unit = { viewModel.addToFavorites(it) }
    val removeFromFav: (Flight) -> Unit = {
        viewModel.removeFromFavorites(it)
    }
    val currentDepartureAirport = viewModel.currentAirport.collectAsStateWithLifecycle()
    var areWeLookingForAllFlights by rememberSaveable {
        mutableStateOf(false)
    }
    val changeAllFlightsFavorites = {lookingForAll: Boolean ->
        areWeLookingForAllFlights = lookingForAll
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
            query = userQuery,
            updateQuery = { viewModel.updateQueryString(it) },
            onInputSubmitted = { viewModel.saveQueryPreference() },
            suggestions = suggestions,
            onAirportClicked = {
                viewModel.provideFlights(it)
                changeAllFlightsFavorites(true)
            },
            flights = flights,
            favoriteFlights = favoriteFlights,
            addToFav = addToFav,
            removeFromFav = removeFromFav,
            currentDepartureAirport = currentDepartureAirport.value,
            areWeLookingForAllFlights = areWeLookingForAllFlights
        )
    }
}

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
    removeFromFav: (Flight) -> Unit,
    currentDepartureAirport: Airport?,
    areWeLookingForAllFlights: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var active by rememberSaveable {
            mutableStateOf(false)
        }
        val onActiveChange = {isActive: Boolean ->
            active = isActive
        }

        Column(
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
                onActiveChange = onActiveChange
            )
            if (!active) {
                if (areWeLookingForAllFlights) {
                    Flights(
                        flights = flights,
                        addToFav = addToFav,
                        removeFromFav = removeFromFav,
                        departureAirport = currentDepartureAirport,
                        areFavorites = false
                    )
                } else {
                    Flights(
                        flights = favoriteFlights,
                        addToFav = addToFav,
                        removeFromFav = removeFromFav,
                        departureAirport = null,
                        areFavorites = true
                    )
                }
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
        onActiveChange = {onActiveChange(it)},
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
            .fillMaxWidth(),
            //.padding(bottom = 16.dp),
        content = {
            Suggestions(
                airports = suggestions,
                onAirportClicked = { airport ->
                    onAirportClicked(airport)
                    onActiveChange(false)
                }
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
    areFavorites: Boolean,
    addToFav: (Flight) -> Unit,
    removeFromFav: (Flight) -> Unit,
    departureAirport: Airport?
) {
    val title = when {
        areFavorites -> stringResource(
            id = R.string.favorite_flights
        )
        else -> stringResource(
            id = R.string.flights_from,
            departureAirport!!.iataCode
        )
    }
    Column {
        Text(
            text = title,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        LazyColumn(
            //contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
    var isStarOn by rememberSaveable {
        mutableStateOf(flight.isFavorite)
    }
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                    top = 4.dp,
                    bottom = 4.dp,
                    start = 4.dp,
                    end = 8.dp
                ),
                horizontalAlignment = Alignment.Start
            ) {
                FlightCardItem(
                    resDepArr = R.string.departure,
                    airport = flight.departureAirport
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlightCardItem(
                    resDepArr = R.string.destination,
                    airport = flight.destinationAirport
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        isStarOn = !isStarOn
                        when {
                            flight.isFavorite -> removeFromFav(flight)
                            else -> addToFav(flight)
                        }
                    },
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 4.dp
                    )
                ) {
                    if (isStarOn) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null
                        )
                    }
                }
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
            text = stringResource(id = resDepArr),
            style = MaterialTheme.typography.bodySmall,
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
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        ) {
            append(airport.iataCode)
        }
        append(" ")
        append(airport.name)
    }
    Text(
        text = airportName,
        style = MaterialTheme.typography.bodySmall
        //textAlign = TextAlign.Justify
    )
}



@Composable
@Preview
fun FlightCardPreview() {
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
                isFavorite = false
            ),
            addToFav = {},
            removeFromFav = {}
        )
    }
}
