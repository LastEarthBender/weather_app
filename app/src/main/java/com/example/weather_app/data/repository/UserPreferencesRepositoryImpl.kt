package com.example.weather_app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weather_app.domain.model.City
import com.example.weather_app.domain.repository.UserPreferencesRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val favoriteCityKey = stringPreferencesKey("favorite_city")
    private val favoriteCitiesKey = stringPreferencesKey("favorite_cities_json")
    private val gson = Gson()

    override suspend fun saveFavoriteCity(cityName: String) {
        context.dataStore.edit { preferences ->
            preferences[favoriteCityKey] = cityName
        }
    }

    override fun getFavoriteCity(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[favoriteCityKey]
        }
    }

    override suspend fun addToFavorites(city: City) {
        context.dataStore.edit { preferences ->
            val currentFavoritesJson = preferences[favoriteCitiesKey] ?: "[]"
            val currentFavorites = try {
                gson.fromJson<List<City>>(
                    currentFavoritesJson, 
                    object : TypeToken<List<City>>() {}.type
                ) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            
            val updatedFavorites = currentFavorites.toMutableList()
            
            // Remove existing city with same key if present
            updatedFavorites.removeAll { it.getSearchKey() == city.getSearchKey() }
            
            // Add new city at the beginning
            updatedFavorites.add(0, city)
            
            // Keep only latest 10 favorites
            val limitedFavorites = updatedFavorites.take(10)
            
            preferences[favoriteCitiesKey] = gson.toJson(limitedFavorites)
        }
    }

    override suspend fun removeFromFavorites(cityKey: String) {
        context.dataStore.edit { preferences ->
            val currentFavoritesJson = preferences[favoriteCitiesKey] ?: "[]"
            val currentFavorites = try {
                gson.fromJson<List<City>>(
                    currentFavoritesJson, 
                    object : TypeToken<List<City>>() {}.type
                ) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            
            val updatedFavorites = currentFavorites.filterNot { it.getSearchKey() == cityKey }
            preferences[favoriteCitiesKey] = gson.toJson(updatedFavorites)
        }
    }

    override fun getFavoriteCities(): Flow<List<City>> {
        return context.dataStore.data.map { preferences ->
            val favoritesJson = preferences[favoriteCitiesKey] ?: "[]"
            try {
                gson.fromJson<List<City>>(
                    favoritesJson, 
                    object : TypeToken<List<City>>() {}.type
                ) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun isCityFavorite(cityKey: String): Boolean {
        val preferences = context.dataStore.data.first()
        val favoritesJson = preferences[favoriteCitiesKey] ?: "[]"
        val favorites = try {
            gson.fromJson<List<City>>(
                favoritesJson, 
                object : TypeToken<List<City>>() {}.type
            ) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        
        return favorites.any { it.getSearchKey() == cityKey }
    }
}
