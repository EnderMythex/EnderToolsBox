package com.example.endertoolsbox.services

import android.location.Location
import com.example.endertoolsbox.models.Weather
import com.example.endertoolsbox.models.WeatherCondition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Random
import kotlin.math.roundToInt

class WeatherService {
    // Cache pour les données météo par région
    private val weatherCache = mutableMapOf<String, Weather>()
    private var lastRefreshTime = 0L
    
    /**
     * Dans une application réelle, cette méthode ferait une requête API vers un service météo
     * Ici, nous simulons la récupération des données météo basées sur la localisation
     */
    suspend fun getWeatherForLocation(location: Location, cityName: String): Weather = withContext(Dispatchers.IO) {
        // Clé de cache basée sur les coordonnées arrondies
        val cacheKey = "${(location.latitude * 10).roundToInt()}:${(location.longitude * 10).roundToInt()}"
        
        // Vérifier si nous avons des données en cache récentes (moins de 30 minutes)
        val currentTime = System.currentTimeMillis()
        if (weatherCache.containsKey(cacheKey) && 
            currentTime - lastRefreshTime < 30 * 60 * 1000) {
            return@withContext weatherCache[cacheKey]!!
        }
        
        // Simuler un délai réseau
        kotlinx.coroutines.delay(300L)
        
        // Génération de données météo plus réalistes
        // Utilisation du hash des coordonnées pour une cohérence par région
        val locationHash = (location.latitude + location.longitude).hashCode()
        val random = Random(locationHash.toLong())
        
        // Température plus réaliste (5-25°C)
        val baseTemp = 9 + (locationHash % 8)
        val tempVariation = random.nextInt(3) - 1 // -1, 0, ou 1 degré de variation
        val temperature = baseTemp + tempVariation
        
        // Condition météo basée sur la région
        val conditionValue = (locationHash % 100) / 25
        val condition = when {
            conditionValue <= 1 -> WeatherCondition.CLEAR
            conditionValue == 2 -> WeatherCondition.CLOUDY
            conditionValue == 3 -> WeatherCondition.RAINY
            else -> WeatherCondition.STORM
        }
        
        val weather = Weather(
            temperature = temperature,
            condition = condition,
            location = cityName
        )
        
        // Mettre à jour le cache
        weatherCache[cacheKey] = weather
        lastRefreshTime = currentTime
        
        return@withContext weather
    }
    
    /**
     * Force une actualisation des données météo en ignorant le cache
     */
    fun invalidateCache() {
        weatherCache.clear()
        lastRefreshTime = 0
    }
    
    /**
     * Dans une version plus avancée, cette méthode utiliserait les coordonnées pour 
     * faire une requête à un API météo comme OpenWeatherMap, AccuWeather, etc.
     */
    suspend fun fetchRealWeatherData(latitude: Double, longitude: Double, cityName: String): Weather {
        // Ici, vous implémenteriez l'appel API à un service météo
        // Pour l'exemple, nous utilisons des données simulées plus réalistes
        return Weather(
            temperature = 9,
            condition = WeatherCondition.CLOUDY,
            location = cityName
        )
    }
} 