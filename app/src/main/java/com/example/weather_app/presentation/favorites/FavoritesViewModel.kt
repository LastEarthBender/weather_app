package com.example.weather_app.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.model.City
import com.example.weather_app.domain.usecase.GetFavoriteCitiesUseCase
import com.example.weather_app.domain.usecase.RemoveFromFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteCitiesUseCase: GetFavoriteCitiesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteCities()
    }

    private fun loadFavoriteCities() {
        viewModelScope.launch {
            getFavoriteCitiesUseCase().collect { favoriteCities ->
                _uiState.value = _uiState.value.copy(
                    favoriteCities = favoriteCities,
                    isLoading = false
                )
            }
        }
    }

    fun removeFromFavorites(city: City) {
        viewModelScope.launch {
            removeFromFavoritesUseCase(city.getSearchKey())
        }
    }
}

data class FavoritesUiState(
    val favoriteCities: List<City> = emptyList(),
    val isLoading: Boolean = true
)
