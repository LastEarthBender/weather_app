package com.example.weather_app.domain.usecase

import com.example.weather_app.domain.model.City
import com.example.weather_app.domain.repository.CityRepository
import com.example.weather_app.domain.util.Resource
import kotlinx.coroutines.delay
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    private val repository: CityRepository
) {
    suspend operator fun invoke(query: String): Resource<List<City>> {
        if (query.isBlank() || query.length < 2) {
            return Resource.Success(emptyList())
        }
        
        // Add small delay to avoid too many API calls while typing
        delay(300)
        
        return repository.searchCities(query.trim())
    }
}
