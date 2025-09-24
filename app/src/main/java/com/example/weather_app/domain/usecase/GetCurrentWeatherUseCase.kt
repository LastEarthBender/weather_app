package com.example.weather_app.domain.usecase

import com.example.weather_app.domain.model.WeatherInfo
import com.example.weather_app.domain.repository.WeatherRepository
import com.example.weather_app.domain.util.Resource
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(cityName: String): Resource<WeatherInfo> {
        if (cityName.isBlank()) {
            return Resource.Error("City name cannot be empty")
        }
        return repository.getCurrentWeather(cityName.trim())
    }

    suspend operator fun invoke(lat: Double, lon: Double): Resource<WeatherInfo> {
        return repository.getCurrentWeatherByCoordinates(lat, lon)
    }
}
