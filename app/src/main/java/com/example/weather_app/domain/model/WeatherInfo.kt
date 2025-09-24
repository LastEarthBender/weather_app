package com.example.weather_app.domain.model

data class WeatherInfo(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val humidity: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String
) {
    fun getTemperatureInCelsius(): Int = (temperature - 273.15).toInt()
    fun getFeelsLikeInCelsius(): Int = (feelsLike - 273.15).toInt()
    fun getMinTemperatureInCelsius(): Int = (minTemperature - 273.15).toInt()
    fun getMaxTemperatureInCelsius(): Int = (maxTemperature - 273.15).toInt()
    fun getFormattedLocation(): String = "$cityName, $country"
}
