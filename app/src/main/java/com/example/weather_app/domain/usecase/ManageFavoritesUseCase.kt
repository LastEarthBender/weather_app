package com.example.weather_app.domain.usecase

import com.example.weather_app.domain.model.City
import com.example.weather_app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteCitiesUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<List<City>> {
        return repository.getFavoriteCities()
    }
}

class AddToFavoritesUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(city: City) {
        repository.addToFavorites(city)
    }
}

class RemoveFromFavoritesUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(cityKey: String) {
        repository.removeFromFavorites(cityKey)
    }
}

class IsCityFavoriteUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(cityKey: String): Boolean {
        return repository.isCityFavorite(cityKey)
    }
}
