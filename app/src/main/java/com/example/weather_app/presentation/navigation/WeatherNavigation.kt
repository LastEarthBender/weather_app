package com.example.weather_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.weather_app.presentation.favorites.FavoritesScreen
import com.example.weather_app.presentation.home.HomeScreen
import com.example.weather_app.presentation.splash.SplashScreen
import com.example.weather_app.presentation.weather_detail.WeatherDetailScreen
import com.example.weather_app.domain.model.City

@Composable
fun WeatherNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { city ->
                    navController.navigate(
                        Screen.WeatherDetail.createRoute(
                            name = city.name,
                            lat = city.latitude,
                            lon = city.longitude,
                            country = city.country,
                            state = city.state
                        )
                    )
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }

        composable(
            route = Screen.WeatherDetail.route,
            arguments = listOf(
                navArgument("name") { type = NavType.StringType; defaultValue = "" },
                navArgument("lat") { type = NavType.FloatType; defaultValue = 0f },
                navArgument("lon") { type = NavType.FloatType; defaultValue = 0f },
                navArgument("country") { type = NavType.StringType; defaultValue = "" },
                navArgument("state") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
            val country = backStackEntry.arguments?.getString("country") ?: ""
            val state = backStackEntry.arguments?.getString("state")?.ifBlank { null }
            WeatherDetailScreen(
                cityName = name,
                latitude = lat,
                longitude = lon,
                country = country,
                state = state,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCityClick = { city: City ->
                    navController.navigate(
                        Screen.WeatherDetail.createRoute(
                            name = city.name,
                            lat = city.latitude,
                            lon = city.longitude,
                            country = city.country,
                            state = city.state
                        )
                    )
                }
            )
        }
    }
}
