package com.example.weather_app.data.repository

import com.example.weather_app.data.mapper.toCities
import com.example.weather_app.data.remote.WeatherApiService
import com.example.weather_app.domain.model.City
import com.example.weather_app.domain.repository.CityRepository
import com.example.weather_app.domain.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService
) : CityRepository {

    override suspend fun searchCities(query: String): Resource<List<City>> {
        return try {
            if (query.length < 2) {
                return Resource.Success(emptyList())
            }
            
            val response = apiService.searchCities(
                query = query,
                limit = 5,
                apiKey = WeatherApiService.API_KEY
            )
            
            if (response.isSuccessful) {
                response.body()?.let { cityResponses ->
                    Resource.Success(cityResponses.toCities())
                } ?: Resource.Error("Empty response body")
            } else {
                when (response.code()) {
                    401 -> Resource.Error("Invalid API key")
                    429 -> Resource.Error("Too many requests")
                    else -> Resource.Error("Search error: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }
}
