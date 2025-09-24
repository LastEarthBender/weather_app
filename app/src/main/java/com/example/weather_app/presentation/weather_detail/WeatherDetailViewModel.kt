package com.example.weather_app.presentation.weather_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.model.City
import com.example.weather_app.domain.model.WeatherInfo
import com.example.weather_app.domain.usecase.AddToFavoritesUseCase
import com.example.weather_app.domain.usecase.GetCurrentWeatherUseCase
import com.example.weather_app.domain.usecase.IsCityFavoriteUseCase
import com.example.weather_app.domain.usecase.RemoveFromFavoritesUseCase
import com.example.weather_app.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val isCityFavoriteUseCase: IsCityFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherDetailUiState())
    val uiState: StateFlow<WeatherDetailUiState> = _uiState.asStateFlow()

    init {
        val nameArg = savedStateHandle.get<String>("name") ?: savedStateHandle.get<String>("cityName") ?: ""
        val latArg = savedStateHandle.get<Float>("lat")?.toDouble() ?: 0.0
        val lonArg = savedStateHandle.get<Float>("lon")?.toDouble() ?: 0.0
        if (latArg != 0.0 || lonArg != 0.0) {
            loadWeatherDetails(latArg, lonArg)
        } else if (nameArg.isNotEmpty()) {
            loadWeatherDetails(nameArg)
        }
    }

    private fun loadWeatherDetails(cityName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = getCurrentWeatherUseCase(cityName)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weatherInfo = result.data,
                        error = null
                    )
                    // Update favorite status once data is loaded
                    val isFav = isCityFavoriteUseCase("${result.data?.cityName?.lowercase()}, ${result.data?.country?.lowercase()}")
                    _uiState.value = _uiState.value.copy(isFavorite = isFav)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun loadWeatherDetails(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = getCurrentWeatherUseCase(lat, lon)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weatherInfo = result.data,
                        error = null
                    )
                    val isFav = isCityFavoriteUseCase("${result.data?.cityName?.lowercase()}, ${result.data?.country?.lowercase()}")
                    _uiState.value = _uiState.value.copy(isFavorite = isFav)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun retry(cityName: String) {
        loadWeatherDetails(cityName)
    }

    fun toggleFavorite() {
        val info = _uiState.value.weatherInfo ?: return
        val city = City(
            name = info.cityName,
            country = info.country,
            state = null,
            latitude = 0.0,
            longitude = 0.0
        )
        viewModelScope.launch {
            val key = city.getSearchKey()
            val currentlyFav = isCityFavoriteUseCase(key)
            if (currentlyFav) {
                removeFromFavoritesUseCase(key)
                _uiState.value = _uiState.value.copy(isFavorite = false)
            } else {
                addToFavoritesUseCase(city)
                _uiState.value = _uiState.value.copy(isFavorite = true)
            }
        }
    }
}

data class WeatherDetailUiState(
    val isLoading: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val error: String? = null,
    val isFavorite: Boolean = false
)
