package com.example.weather_app.domain.model

data class City(
    val name: String,
    val country: String,
    val state: String?,
    val latitude: Double,
    val longitude: Double
) {
    fun getDisplayName(): String {
        return if (state != null) {
            "$name, $state, $country"
        } else {
            "$name, $country"
        }
    }
    
    fun getSearchKey(): String = "${name.lowercase()}, ${country.lowercase()}"
}
