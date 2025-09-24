package com.example.weather_app.domain.repository

import com.example.weather_app.domain.model.City
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    suspend fun saveFavoriteCity(cityName: String)
    fun getFavoriteCity(): Flow<String?>
    
    // New methods for multiple favorites
    suspend fun addToFavorites(city: City)
    suspend fun removeFromFavorites(cityKey: String)
    fun getFavoriteCities(): Flow<List<City>>
    suspend fun isCityFavorite(cityKey: String): Boolean
}
