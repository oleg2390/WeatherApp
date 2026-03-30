package com.example.weatherapp.feature.city.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.core.common.AppResult
import com.example.weatherapp.core.common.UiText
import com.example.weatherapp.feature.city.domain.model.City
import com.example.weatherapp.feature.city.domain.usecase.GetSavedCitiesUseCase
import com.example.weatherapp.feature.city.domain.usecase.SaveSelectedCityUseCase
import com.example.weatherapp.feature.city.domain.usecase.SearchCitiesUseCase
import com.example.weatherapp.feature.city.presentation.model.CityUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val searchCitiesUseCase: SearchCitiesUseCase,
    private val saveSelectedCityUseCase: SaveSelectedCityUseCase,
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CityState())
    val state: StateFlow<CityState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CityEffect>()
    val effect: SharedFlow<CityEffect> = _effect.asSharedFlow()

    private var searchJob: Job? = null
    private var lastSearchedQuery: String? = null

    init {
        observeSavedCities()
    }

    fun onIntent(intent: CityIntent) {
        when (intent) {
            CityIntent.OnBackClick -> emitBack()
            is CityIntent.OnQueryChanged -> onQueryChanged(intent.query)
            CityIntent.OnSearchClick -> search()
            is CityIntent.OnCityClick -> selectCity(intent.city)
        }
    }

    private fun observeSavedCities() {
        viewModelScope.launch {
            getSavedCitiesUseCase().collect { cities ->
                _state.update {
                    it.copy(savedCities = cities.map { city -> city.toUiItem() })
                }
            }
        }
    }

    private fun onQueryChanged(query: String) {
        val previousQuery = state.value.query
        _state.update {
            it.copy(
                query = query,
                searchState = if (query.isBlank() || query != previousQuery) {
                    CitySearchState.Idle
                } else {
                    it.searchState
                }
            )
        }
    }

    private fun search() {
        val query = state.value.query.trim()
        if (query.isBlank()) {
            _state.update { it.copy(searchState = CitySearchState.Idle) }
            return
        }

        if (lastSearchedQuery == query && state.value.searchState is CitySearchState.Content) {
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.update { it.copy(searchState = CitySearchState.Loading) }

            when (val result = searchCitiesUseCase(query)) {
                is AppResult.Success -> {
                    val items = result.data.map { city -> city.toUiItem() }
                    _state.update {
                        it.copy(
                            searchState = if (items.isEmpty()) {
                                CitySearchState.Empty
                            } else {
                                CitySearchState.Content(items)
                            }
                        )
                    }
                    lastSearchedQuery = query
                }

                is AppResult.Error -> {
                    _state.update {
                        it.copy(
                            searchState = CitySearchState.Error(
                                UiText.StringResource(R.string.city_search_error)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun selectCity(city: CityUiItem) {
        viewModelScope.launch {
            saveSelectedCityUseCase(
                City(
                    id = city.savedCityId,
                    name = city.name,
                    country = city.country,
                    lat = city.lat,
                    lon = city.lon,
                    isSelected = true
                )
            )
            _effect.emit(CityEffect.NavigateBack)
        }
    }

    private fun emitBack() {
        viewModelScope.launch {
            _effect.emit(CityEffect.NavigateBack)
        }
    }

    private fun City.toUiItem(): CityUiItem = CityUiItem(
        id = id?.toString() ?: "${lat}_${lon}",
        savedCityId = id,
        title = "$name, $country",
        subtitle = formatCoordinates(lat, lon),
        name = name,
        country = country,
        lat = lat,
        lon = lon,
        isSelected = isSelected
    )

    private fun formatCoordinates(lat: Double, lon: Double): String {
        val latText = "%.2f".format(Locale.US, lat)
        val lonText = "%.2f".format(Locale.US, lon)
        return "$latText, $lonText"
    }
}