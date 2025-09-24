package com.example.weather_app.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    // Use query params so we can pass coordinates to disambiguate cities with same name
    object WeatherDetail : Screen("weather_detail?name={name}&lat={lat}&lon={lon}&country={country}&state={state}") {
        fun createRoute(
            name: String,
            lat: Double,
            lon: Double,
            country: String,
            state: String?
        ): String {
            val s = state ?: ""
            return "weather_detail?name=${name}&lat=${lat}&lon=${lon}&country=${country}&state=${s}"
        }
    }
    object Favorites : Screen("favorites")
}
