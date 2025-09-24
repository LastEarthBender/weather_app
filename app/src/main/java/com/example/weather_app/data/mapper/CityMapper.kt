package com.example.weather_app.data.mapper

import com.example.weather_app.data.model.CitySearchResponse
import com.example.weather_app.domain.model.City

fun CitySearchResponse.toCity(): City {
    return City(
        name = name,
        country = country,
        state = state,
        latitude = latitude,
        longitude = longitude
    )
}

fun List<CitySearchResponse>.toCities(): List<City> {
    return map { it.toCity() }
}
