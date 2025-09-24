package com.example.weather_app.data.repository

import com.example.weather_app.data.mapper.toWeatherInfo
import com.example.weather_app.data.remote.WeatherApiService
import com.example.weather_app.domain.model.WeatherInfo
import com.example.weather_app.domain.repository.WeatherRepository
import com.example.weather_app.domain.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService
) : WeatherRepository {

    override suspend fun getCurrentWeather(cityName: String): Resource<WeatherInfo> {
        return try {
            val response = apiService.getCurrentWeather(
                cityName = cityName,
                apiKey = WeatherApiService.API_KEY
            )
            
            if (response.isSuccessful) {
                response.body()?.let { weatherResponse ->
                    Resource.Success(weatherResponse.toWeatherInfo())
                } ?: Resource.Error("Empty response body")
            } else {
                when (response.code()) {
                    404 -> Resource.Error("City not found")
                    401 -> Resource.Error("Invalid API key")
                    else -> Resource.Error("Network error: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }

    override suspend fun getCurrentWeatherByCoordinates(lat: Double, lon: Double): Resource<WeatherInfo> {
        return try {
            val response = apiService.getCurrentWeatherByCoordinates(
                latitude = lat,
                longitude = lon,
                apiKey = WeatherApiService.API_KEY
            )
            if (response.isSuccessful) {
                response.body()?.let { weatherResponse ->
                    Resource.Success(weatherResponse.toWeatherInfo())
                } ?: Resource.Error("Empty response body")
            } else {
                when (response.code()) {
                    401 -> Resource.Error("Invalid API key")
                    else -> Resource.Error("Network error: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }
}
