package com.example.weather_app.presentation.home

import com.example.weather_app.domain.model.WeatherInfo
import com.example.weather_app.domain.usecase.GetCurrentWeatherUseCase
import com.example.weather_app.domain.usecase.GetFavoriteCityUseCase
import com.example.weather_app.domain.usecase.SaveFavoriteCityUseCase
import com.example.weather_app.domain.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase
    private lateinit var saveFavoriteCityUseCase: SaveFavoriteCityUseCase
    private lateinit var getFavoriteCityUseCase: GetFavoriteCityUseCase
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getCurrentWeatherUseCase = mockk()
        saveFavoriteCityUseCase = mockk(relaxed = true)
        getFavoriteCityUseCase = mockk()

        every { getFavoriteCityUseCase() } returns flowOf(null)

        viewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            saveFavoriteCityUseCase,
            getFavoriteCityUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val initialState = viewModel.uiState.value
        assertEquals("", initialState.cityName)
        assertFalse(initialState.isLoading)
        assertNull(initialState.weatherInfo)
        assertNull(initialState.error)
        assertFalse(initialState.isFavorite)
    }

    @Test
    fun `onCityNameChanged updates city name`() {
        val newCityName = "London"
        viewModel.onCityNameChanged(newCityName)
        assertEquals(newCityName, viewModel.uiState.value.cityName)
    }

    @Test
    fun `searchWeather with empty city name shows error`() {
        viewModel.searchWeather()
        assertEquals("Please enter a city name", viewModel.uiState.value.error)
    }

    @Test
    fun `searchWeather with valid city name calls use case and updates state`() = runTest {
        // Given
        val cityName = "London"
        val mockWeatherInfo = WeatherInfo(
            cityName = "London",
            country = "UK",
            temperature = 293.15,
            feelsLike = 295.15,
            minTemperature = 290.15,
            maxTemperature = 296.15,
            humidity = 65,
            weatherMain = "Clear",
            weatherDescription = "clear sky",
            weatherIcon = "01d"
        )
        coEvery { getCurrentWeatherUseCase(cityName) } returns Resource.Success(mockWeatherInfo)

        // When
        viewModel.onCityNameChanged(cityName)
        viewModel.searchWeather()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(mockWeatherInfo, state.weatherInfo)
        assertNull(state.error)
    }

    @Test
    fun `searchWeather with error updates error state`() = runTest {
        // Given
        val cityName = "InvalidCity"
        val errorMessage = "City not found"
        coEvery { getCurrentWeatherUseCase(cityName) } returns Resource.Error(errorMessage)

        // When
        viewModel.onCityNameChanged(cityName)
        viewModel.searchWeather()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertNull(state.weatherInfo)
    }

    @Test
    fun `saveFavoriteCity calls use case and updates state`() = runTest {
        // Given
        val cityName = "London"
        viewModel.onCityNameChanged(cityName)

        // When
        viewModel.saveFavoriteCity()
        advanceUntilIdle()

        // Then
        coVerify { saveFavoriteCityUseCase(cityName) }
        assertTrue(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun `favorite city is loaded on initialization`() = runTest {
        // Given
        val favoriteCity = "Paris"
        every { getFavoriteCityUseCase() } returns flowOf(favoriteCity)

        // When
        val newViewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            saveFavoriteCityUseCase,
            getFavoriteCityUseCase
        )
        advanceUntilIdle()

        // Then
        val state = newViewModel.uiState.value
        assertEquals(favoriteCity, state.cityName)
        assertTrue(state.isFavorite)
    }

    @Test
    fun `clearError removes error from state`() {
        // Given - set an error first
        viewModel.searchWeather() // This will set an error because city name is empty

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.uiState.value.error)
    }
}
