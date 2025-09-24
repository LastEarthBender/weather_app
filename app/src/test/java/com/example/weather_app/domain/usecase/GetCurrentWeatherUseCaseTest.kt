package com.example.weather_app.domain.usecase

import com.example.weather_app.domain.model.WeatherInfo
import com.example.weather_app.domain.repository.WeatherRepository
import com.example.weather_app.domain.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetCurrentWeatherUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetCurrentWeatherUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetCurrentWeatherUseCase(repository)
    }

    @Test
    fun `when city name is blank, returns error`() = runTest {
        // Given
        val blankCityName = "   "

        // When
        val result = useCase(blankCityName)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("City name cannot be empty", result.message)
    }

    @Test
    fun `when city name is valid, returns success`() = runTest {
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
        coEvery { repository.getCurrentWeather(cityName) } returns Resource.Success(mockWeatherInfo)

        // When
        val result = useCase(cityName)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(mockWeatherInfo, result.data)
    }

    @Test
    fun `when repository returns error, returns error`() = runTest {
        // Given
        val cityName = "London"
        val errorMessage = "Network error"
        coEvery { repository.getCurrentWeather(cityName) } returns Resource.Error(errorMessage)

        // When
        val result = useCase(cityName)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, result.message)
    }

    @Test
    fun `trims whitespace from city name`() = runTest {
        // Given
        val cityNameWithSpaces = "  London  "
        val trimmedCityName = "London"
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
        coEvery { repository.getCurrentWeather(trimmedCityName) } returns Resource.Success(mockWeatherInfo)

        // When
        val result = useCase(cityNameWithSpaces)

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(mockWeatherInfo, result.data)
    }
}
