package com.example.weatherapp.feature.city.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.core.common.asString
import com.example.weatherapp.feature.city.presentation.model.CityUiItem
import com.example.weatherapp.ui.component.WeatherTopBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CityRoute(
    onBack: () -> Unit,
    viewModel: CityViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                CityEffect.NavigateBack -> onBack()
            }
        }
    }

    CityScreen(
        state = state.value,
        onQueryChanged = { viewModel.onIntent(CityIntent.OnQueryChanged(it)) },
        onSearchClick = { viewModel.onIntent(CityIntent.OnSearchClick) },
        onCityClick = { viewModel.onIntent(CityIntent.OnCityClick(it)) },
        onBackClick = { viewModel.onIntent(CityIntent.OnBackClick) }
    )
}

@Composable
fun CityScreen(
    state: CityState,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCityClick: (CityUiItem) -> Unit,
    onBackClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.space_16)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_12)),
    ) {
        WeatherTopBar(
            title = stringResource(R.string.city_title),
            onBackClick = onBackClick
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.elevation_2))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.space_12)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_8)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChanged,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    label = { Text(stringResource(R.string.city_query_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (state.query.isBlank()) {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            } else {
                                keyboardController?.hide()
                                onSearchClick()
                            }
                        }
                    )
                )

                Button(
                    onClick = {
                        if (state.query.isBlank()) {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        } else {
                            keyboardController?.hide()
                            onSearchClick()
                        }
                    }
                ) {
                    Text(stringResource(R.string.city_search_button))
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_8))
        ) {
            item {
                Text(
                    text = stringResource(R.string.city_saved_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                AnimatedVisibility(
                    visible = state.savedCities.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(stringResource(R.string.city_no_saved))
                }
            }
            items(
                items = state.savedCities,
                key = { "saved_${it.id}" }
            ) { city ->
                CityListItem(
                    city = city,
                    onClick = onCityClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.space_8)))
                Text(
                    text = stringResource(R.string.city_results_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            when (val searchResult = state.searchState) {
                CitySearchState.Idle -> {
                    item {
                        Text(text = stringResource(R.string.city_search_idle))
                    }
                }

                CitySearchState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                CitySearchState.Empty -> {
                    item {
                        Text(text = stringResource(R.string.city_search_empty))
                    }
                }

                is CitySearchState.Error -> {
                    item {
                        Text(searchResult.message.asString())
                    }
                }

                is CitySearchState.Content -> {
                    items(
                        items = searchResult.cities,
                        key = { "search_${it.id}" }
                    ) { city ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            CityListItem(
                                city = city,
                                onClick = onCityClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityListItem(
    city: CityUiItem,
    onClick: (CityUiItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.space_2))
            .clickable { onClick(city) },
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.elevation_1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.space_12),
                    vertical = dimensionResource(R.dimen.space_10)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_2))
            ) {
                Text(
                    text = city.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = city.subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (city.isSelected) {
                Text(
                    text = stringResource(R.string.city_selected_badge),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}