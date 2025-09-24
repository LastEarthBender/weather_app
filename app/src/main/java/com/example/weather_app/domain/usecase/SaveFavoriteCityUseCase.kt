package com.example.weather_app.domain.usecase

import com.example.weather_app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveFavoriteCityUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(cityName: String) {
        if (cityName.isNotBlank()) {
            repository.saveFavoriteCity(cityName.trim())
        }
    }
}
