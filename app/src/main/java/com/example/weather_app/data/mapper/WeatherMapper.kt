package com.example.weather_app.data.mapper

import com.example.weather_app.data.model.WeatherResponse
import com.example.weather_app.domain.model.WeatherInfo

fun WeatherResponse.toWeatherInfo(): WeatherInfo {
    return WeatherInfo(
        cityName = name,
        country = sys.country,
        temperature = main.temp,
        feelsLike = main.feelsLike,
        minTemperature = main.tempMin,
        maxTemperature = main.tempMax,
        humidity = main.humidity,
        weatherMain = weather.firstOrNull()?.main ?: "",
        weatherDescription = weather.firstOrNull()?.description ?: "",
        weatherIcon = weather.firstOrNull()?.icon ?: ""
    )
}
