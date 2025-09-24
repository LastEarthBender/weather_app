package com.example.weather_app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherInfoTest {

    @Test
    fun `getTemperatureInCelsius converts Kelvin to Celsius correctly`() {
        // Given
        val weatherInfo = WeatherInfo(
            cityName = "London",
            country = "UK",
            temperature = 293.15, // 20°C in Kelvin
            feelsLike = 295.15,
            minTemperature = 290.15,
            maxTemperature = 296.15,
            humidity = 65,
            weatherMain = "Clear",
            weatherDescription = "clear sky",
            weatherIcon = "01d"
        )

        // When
        val celsius = weatherInfo.getTemperatureInCelsius()

        // Then
        assertEquals(20, celsius)
    }

    @Test
    fun `getFeelsLikeInCelsius converts Kelvin to Celsius correctly`() {
        // Given
        val weatherInfo = WeatherInfo(
            cityName = "London",
            country = "UK",
            temperature = 293.15,
            feelsLike = 295.15, // 22°C in Kelvin
            minTemperature = 290.15,
            maxTemperature = 296.15,
            humidity = 65,
            weatherMain = "Clear",
            weatherDescription = "clear sky",
            weatherIcon = "01d"
        )

        // When
        val feelsLike = weatherInfo.getFeelsLikeInCelsius()

        // Then
        assertEquals(22, feelsLike)
    }

    @Test
    fun `getMinTemperatureInCelsius converts Kelvin to Celsius correctly`() {
        // Given
        val weatherInfo = WeatherInfo(
            cityName = "London",
            country = "UK",
            temperature = 293.15,
            feelsLike = 295.15,
            minTemperature = 290.15, // 17°C in Kelvin
            maxTemperature = 296.15,
            humidity = 65,
            weatherMain = "Clear",
            weatherDescription = "clear sky",
            weatherIcon = "01d"
        )

        // When
        val minTemp = weatherInfo.getMinTemperatureInCelsius()

        // Then
        assertEquals(17, minTemp)
    }

    @Test
    fun `getMaxTemperatureInCelsius converts Kelvin to Celsius correctly`() {
        // Given
        val weatherInfo = WeatherInfo(
            cityName = "London",
            country = "UK",
            temperature = 293.15,
            feelsLike = 295.15,
            minTemperature = 290.15,
            maxTemperature = 296.15, // 23°C in Kelvin
            humidity = 65,
            weatherMain = "Clear",
            weatherDescription = "clear sky",
            weatherIcon = "01d"
        )

        // When
        val maxTemp = weatherInfo.getMaxTemperatureInCelsius()

        // Then
        assertEquals(23, maxTemp)
    }

    @Test
    fun `getFormattedLocation formats city and country correctly`() {
        // Given
        val weatherInfo = WeatherInfo(
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

        // When
        val formattedLocation = weatherInfo.getFormattedLocation()

        // Then
        assertEquals("London, UK", formattedLocation)
    }

    @Test
    fun `temperature conversion handles negative Celsius values correctly`() {
        // Given
        val weatherInfo = WeatherInfo(
            cityName = "Moscow",
            country = "RU",
            temperature = 263.15, // -10°C in Kelvin
            feelsLike = 260.15,
            minTemperature = 258.15,
            maxTemperature = 268.15,
            humidity = 80,
            weatherMain = "Snow",
            weatherDescription = "light snow",
            weatherIcon = "13d"
        )

        // When & Then
        assertEquals(-10, weatherInfo.getTemperatureInCelsius())
        assertEquals(-13, weatherInfo.getFeelsLikeInCelsius())
        assertEquals(-15, weatherInfo.getMinTemperatureInCelsius())
        assertEquals(-5, weatherInfo.getMaxTemperatureInCelsius())
    }
}
