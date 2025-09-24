package com.example.weather_app.data.repository

import com.example.weather_app.data.model.Main
import com.example.weather_app.data.model.Sys
import com.example.weather_app.data.model.Weather
import com.example.weather_app.data.model.WeatherResponse
import com.example.weather_app.data.remote.WeatherApiService
import com.example.weather_app.domain.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherRepositoryImplTest {

    private lateinit var apiService: WeatherApiService
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = WeatherRepositoryImpl(apiService)
    }

    @Test
    fun `getCurrentWeather returns success when API call succeeds`() = runTest {
        // Given
        val cityName = "London"
        val mockResponse = WeatherResponse(
            weather = listOf(
                Weather(
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            main = Main(
                temp = 293.15,
                feelsLike = 295.15,
                tempMin = 290.15,
                tempMax = 296.15,
                humidity = 65
            ),
            name = "London",
            sys = Sys(country = "UK")
        )
        val response = Response.success(mockResponse)
        coEvery { apiService.getCurrentWeather(cityName, WeatherApiService.API_KEY) } returns response

        // When
        val result = repository.getCurrentWeather(cityName)

        // Then
        assertTrue(result is Resource.Success)
        val weatherInfo = result.data!!
        assertEquals("London", weatherInfo.cityName)
        assertEquals("UK", weatherInfo.country)
        assertEquals(293.15, weatherInfo.temperature, 0.01)
        assertEquals("Clear", weatherInfo.weatherMain)
    }

    @Test
    fun `getCurrentWeather returns error when API returns 404`() = runTest {
        // Given
        val cityName = "InvalidCity"
        val errorResponse = Response.error<WeatherResponse>(404, "".toResponseBody())
        coEvery { apiService.getCurrentWeather(cityName, WeatherApiService.API_KEY) } returns errorResponse

        // When
        val result = repository.getCurrentWeather(cityName)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("City not found", result.message)
    }

    @Test
    fun `getCurrentWeather returns error when API returns 401`() = runTest {
        // Given
        val cityName = "London"
        val errorResponse = Response.error<WeatherResponse>(401, "".toResponseBody())
        coEvery { apiService.getCurrentWeather(cityName, WeatherApiService.API_KEY) } returns errorResponse

        // When
        val result = repository.getCurrentWeather(cityName)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Invalid API key", result.message)
    }

    @Test
    fun `getCurrentWeather returns error when response body is null`() = runTest {
        // Given
        val cityName = "London"
        val response = Response.success<WeatherResponse>(null)
        coEvery { apiService.getCurrentWeather(cityName, WeatherApiService.API_KEY) } returns response

        // When
        val result = repository.getCurrentWeather(cityName)

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Empty response body", result.message)
    }

    @Test
    fun `getCurrentWeather returns error when exception is thrown`() = runTest {
        // Given
        val cityName = "London"
        val exception = Exception("Network error")
        coEvery { apiService.getCurrentWeather(cityName, WeatherApiService.API_KEY) } throws exception

        // When
        val result = repository.getCurrentWeather(cityName)

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message!!.contains("Network error"))
    }
}
