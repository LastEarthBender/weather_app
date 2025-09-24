package com.example.weather_app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.model.City
import com.example.weather_app.domain.model.CityWeatherCard
import com.example.weather_app.domain.model.WeatherInfo
import com.example.weather_app.domain.usecase.AddToFavoritesUseCase
import com.example.weather_app.domain.usecase.GetCurrentWeatherUseCase
import com.example.weather_app.domain.usecase.GetFavoriteCityUseCase
import com.example.weather_app.domain.usecase.IsCityFavoriteUseCase
import com.example.weather_app.domain.usecase.RemoveFromFavoritesUseCase
import com.example.weather_app.domain.usecase.SaveFavoriteCityUseCase
import com.example.weather_app.domain.usecase.SearchCitiesUseCase
import com.example.weather_app.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val saveFavoriteCityUseCase: SaveFavoriteCityUseCase,
    private val getFavoriteCityUseCase: GetFavoriteCityUseCase,
    private val searchCitiesUseCase: SearchCitiesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val isCityFavoriteUseCase: IsCityFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFavoriteCity()
    }

    fun onCityNameChanged(cityName: String) {
        _uiState.value = _uiState.value.copy(
            cityName = cityName,
            showCityCards = cityName.isNotBlank()
        )

        // Cancel previous search
        searchJob?.cancel()

        // Start new search if text is not empty
        if (cityName.isNotBlank() && cityName.length >= 2) {
            searchJob = viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isSearchingCities = true)

                when (val result = searchCitiesUseCase(cityName)) {
                    is Resource.Success -> {
                        val cities = result.data ?: emptyList()

                        // Create city weather cards with initial state
                        val cityCards = cities.take(3).map { city -> // Limit to 3 for performance
                            CityWeatherCard(
                                city = city,
                                weatherInfo = null,
                                isFavorite = false,
                                isLoadingWeather = true,
                                weatherError = null
                            )
                        }

                        _uiState.value = _uiState.value.copy(
                            cityWeatherCards = cityCards,
                            isSearchingCities = false
                        )

                        // Fetch weather and favorite status for each city using coordinates
                        cityCards.forEach { card ->
                            fetchWeatherAndFavoriteStatus(card.city)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            cityWeatherCards = emptyList(),
                            isSearchingCities = false
                        )
                    }
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isSearchingCities = true)
                    }
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(
                cityWeatherCards = emptyList(),
                isSearchingCities = false,
                showCityCards = false
            )
        }
    }

    private suspend fun fetchWeatherAndFavoriteStatus(city: City) {
        viewModelScope.launch {
            // Check favorite status
            val isFavorite = isCityFavoriteUseCase(city.getSearchKey())

            // Fetch weather
            when (val weatherResult = getCurrentWeatherUseCase(city.latitude, city.longitude)) {
                is Resource.Success -> {
                    updateCityCard(city) { card ->
                        card.copy(
                            weatherInfo = weatherResult.data,
                            isFavorite = isFavorite,
                            isLoadingWeather = false,
                            weatherError = null
                        )
                    }
                }
                is Resource.Error -> {
                    updateCityCard(city) { card ->
                        card.copy(
                            weatherInfo = null,
                            isFavorite = isFavorite,
                            isLoadingWeather = false,
                            weatherError = weatherResult.message
                        )
                    }
                }
                is Resource.Loading -> {
                    updateCityCard(city) { card ->
                        card.copy(
                            isFavorite = isFavorite,
                            isLoadingWeather = true
                        )
                    }
                }
            }
        }
    }

    private fun updateCityCard(city: City, transform: (CityWeatherCard) -> CityWeatherCard) {
        val currentCards = _uiState.value.cityWeatherCards
        val updatedCards = currentCards.map { card ->
            if (card.city.getSearchKey() == city.getSearchKey()) {
                transform(card)
            } else {
                card
            }
        }
        _uiState.value = _uiState.value.copy(cityWeatherCards = updatedCards)
    }

    fun onCityCardClick(city: City) {
        _uiState.value = _uiState.value.copy(
            cityName = city.name,
            showCityCards = false,
            cityWeatherCards = emptyList(),
            selectedCity = city
        )

        // Auto-search weather for selected city
        searchWeatherForCity(city)
    }

    fun hideCityCards() {
        _uiState.value = _uiState.value.copy(showCityCards = false)
    }

    fun searchWeather() {
        val cityName = _uiState.value.cityName
        if (cityName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a city name")
            return
        }
        searchWeatherForCity(cityName)
    }

    private fun searchWeatherForCity(cityName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = getCurrentWeatherUseCase(cityName)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weatherInfo = result.data,
                        error = null,
                        showCityCards = false
                    )
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

    private fun searchWeatherForCity(city: City) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = getCurrentWeatherUseCase(city.latitude, city.longitude)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weatherInfo = result.data,
                        error = null,
                        showCityCards = false,
                        selectedCity = city
                    )
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

    fun toggleCityFavorite(city: City) {
        viewModelScope.launch {
            val currentCard = _uiState.value.cityWeatherCards.find { 
                it.city.getSearchKey() == city.getSearchKey() 
            }
            
            if (currentCard?.isFavorite == true) {
                // Remove from favorites
                removeFromFavoritesUseCase(city.getSearchKey())
                updateCityCard(city) { card ->
                    card.copy(isFavorite = false)
                }
            } else {
                // Add to favorites
                addToFavoritesUseCase(city)
                updateCityCard(city) { card ->
                    card.copy(isFavorite = true)
                }
            }
        }
    }

    fun toggleCurrentFavorite() {
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
            val isFav = isCityFavoriteUseCase(key)
            if (isFav) {
                removeFromFavoritesUseCase(key)
                _uiState.value = _uiState.value.copy(isCurrentFavorite = false)
            } else {
                addToFavoritesUseCase(city)
                _uiState.value = _uiState.value.copy(isCurrentFavorite = true)
            }
        }
    }

    fun saveFavoriteCity() {
        val cityName = _uiState.value.cityName
        if (cityName.isNotBlank()) {
            viewModelScope.launch {
                saveFavoriteCityUseCase(cityName)
            }
        }
    }

    private fun loadFavoriteCity() {
        viewModelScope.launch {
            getFavoriteCityUseCase().collect { favoriteCity ->
                favoriteCity?.let {
                    _uiState.value = _uiState.value.copy(cityName = it)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HomeUiState(
    val cityName: String = "",
    val isLoading: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val error: String? = null,
    val cityWeatherCards: List<CityWeatherCard> = emptyList(),
    val isSearchingCities: Boolean = false,
    val showCityCards: Boolean = false,
    val selectedCity: City? = null,
    val isCurrentFavorite: Boolean = false
)
