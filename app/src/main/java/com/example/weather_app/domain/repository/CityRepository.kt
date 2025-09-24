package com.example.weather_app.domain.repository

import com.example.weather_app.domain.model.City
import com.example.weather_app.domain.util.Resource

interface CityRepository {
    suspend fun searchCities(query: String): Resource<List<City>>
}
