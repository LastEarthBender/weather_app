package com.example.weather_app.domain.model

data class CityWeatherCard(
    val city: City,
    val weatherInfo: WeatherInfo? = null,
    val isFavorite: Boolean = false,
    val isLoadingWeather: Boolean = false,
    val weatherError: String? = null
) {
    fun getDisplayName(): String = city.getDisplayName()
    fun getSearchKey(): String = city.getSearchKey()
}
