package com.example.endertoolsbox.models

data class Weather(
    val temperature: Int,
    val condition: WeatherCondition,
    val location: String,
    val isOffline: Boolean = false
)

enum class WeatherCondition {
    CLEAR, CLOUDY, RAINY, STORM, OFFLINE
}