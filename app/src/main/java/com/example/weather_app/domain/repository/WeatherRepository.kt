package com.example.weather_app.domain.repository

import com.example.weather_app.domain.model.WeatherInfo
import com.example.weather_app.domain.util.Resource

interface WeatherRepository {
    suspend fun getCurrentWeather(cityName: String): Resource<WeatherInfo>
    suspend fun getCurrentWeatherByCoordinates(lat: Double, lon: Double): Resource<WeatherInfo>
}
