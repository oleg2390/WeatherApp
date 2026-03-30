package com.example.weatherapp.feature.home.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.core.common.UiText
import com.example.weatherapp.core.common.asString
import com.example.weatherapp.feature.home.presentation.model.DailyWeatherUi
import com.example.weatherapp.feature.home.presentation.model.HomeWeatherUi
import com.example.weatherapp.feature.home.presentation.model.HourlyWeatherUi
import com.example.weatherapp.ui.component.WeatherTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
    onOpenCity: () -> Unit,
    viewModel: HomeViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.any { it }
        viewModel.onIntent(HomeIntent.OnLocationPermissionResult(granted))
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onIntent(HomeIntent.OnRefresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                HomeEffect.NavigateToCity -> onOpenCity()
                HomeEffect.RequestLocationPermission -> {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            }
        }
    }

    HomeScreen(
        state = state.value,
        onOpenCityClick = { viewModel.onIntent(HomeIntent.OnOpenClick) },
        onRefreshClick = { viewModel.onIntent(HomeIntent.OnRefresh) },
        onRequestLocationClick = { viewModel.onIntent(HomeIntent.OnRequestLocationPermission) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onOpenCityClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onRequestLocationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.space_16)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_12)),
    ) {
        WeatherTopBar(title = state.title.asString())

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            PullToRefreshBox(
                isRefreshing = state.contentState is HomeContentState.Loading,
                onRefresh = onRefreshClick,
                modifier = Modifier.fillMaxSize()
            ) {
                when (val contentState = state.contentState) {
                    HomeContentState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is HomeContentState.Empty -> {
                        StatusCard(
                            icon = "\uD83D\uDCCD",
                            title = stringResource(R.string.home_empty_title),
                            message = contentState.message,
                            showLocationAction = state.showLocationAction,
                            onRequestLocationClick = onRequestLocationClick
                        )
                    }

                    is HomeContentState.Error -> {
                        StatusCard(
                            icon = "\u26A0\uFE0F",
                            title = stringResource(R.string.home_error_title),
                            message = contentState.message,
                            showLocationAction = state.showLocationAction,
                            onRequestLocationClick = onRequestLocationClick
                        )
                    }

                    is HomeContentState.Content -> {
                        HomeContent(weather = contentState.weather)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_8))
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onOpenCityClick
            ) {
                Text(text = stringResource(R.string.home_choose_city))
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onRefreshClick
            ) {
                Text(text = stringResource(R.string.home_refresh))
            }
        }
    }
}

@Composable
private fun HomeContent(
    weather: HomeWeatherUi
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_12)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_8))
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.elevation_2))
            ) {
                Row(
                    modifier = Modifier.padding(dimensionResource(R.dimen.space_16)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_6))
                    ) {
                        Text(
                            text = stringResource(R.string.home_current_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = weather.current.temp,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = weather.current.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.space_6)))
                        Text(stringResource(R.string.home_feels_like_label, weather.current.feelsLike))
                        Text(stringResource(R.string.home_humidity_label, weather.current.humidityPercent))
                        Text(stringResource(R.string.home_wind_label, weather.current.windSpeed))
                    }
                    WeatherIcon(
                        iconUrl = weather.current.iconUrl,
                        contentDescription = weather.current.description,
                        sizeDp = dimensionResource(R.dimen.home_icon_current_size)
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.home_hourly_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_8)),
                contentPadding = PaddingValues(end = dimensionResource(R.dimen.space_4))
            ) {
                items(
                    items = weather.hourly,
                    key = { it.id }
                ) { hour ->
                    HourItem(hour = hour)
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.home_daily_title),
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(
            items = weather.daily,
            key = { it.id }
        ) { day ->
            DayItem(day = day)
        }
    }
}

@Composable
private fun StatusCard(
    message: UiText,
    icon: String,
    title: String,
    showLocationAction: Boolean,
    onRequestLocationClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.space_8)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.elevation_2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.space_20)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_10))
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = message.asString(),
                style = MaterialTheme.typography.bodyMedium
            )
            if (showLocationAction) {
                Button(onClick = onRequestLocationClick) {
                    Text(stringResource(R.string.home_allow_location))
                }
            }
        }
    }
}

@Composable
private fun DayItem(
    day: DailyWeatherUi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.elevation_1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.space_12),
                    vertical = dimensionResource(R.dimen.space_10)
                ),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_10)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherIcon(
                iconUrl = day.iconUrl,
                contentDescription = day.description,
                sizeDp = dimensionResource(R.dimen.home_icon_item_size)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_2))
            ) {
                Text(day.day, style = MaterialTheme.typography.bodyLarge)
                Text(day.description, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = "${day.minTemp} / ${day.maxTemp}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun HourItem(
    hour: HourlyWeatherUi
) {
    Card(
        modifier = Modifier
            .size(
                width = dimensionResource(R.dimen.home_hour_item_width),
                height = dimensionResource(R.dimen.home_hour_item_height)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.elevation_1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = dimensionResource(R.dimen.space_10),
                    horizontal = dimensionResource(R.dimen.space_8)
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_6)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = hour.time,
                style = MaterialTheme.typography.bodyMedium
            )
            WeatherIcon(
                iconUrl = hour.iconUrl,
                contentDescription = hour.description,
                sizeDp = 36.dp
            )
            Text(
                text = hour.temp,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = hour.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun WeatherIcon(
    iconUrl: String,
    contentDescription: String,
    sizeDp: Dp
) {
    AsyncImage(
        model = iconUrl,
        contentDescription = contentDescription,
        modifier = Modifier
            .size(sizeDp)
            .clip(MaterialTheme.shapes.small)
    )
}