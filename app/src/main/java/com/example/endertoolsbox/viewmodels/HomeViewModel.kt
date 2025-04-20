package com.example.endertoolsbox.viewmodels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.endertoolsbox.models.Weather
import com.example.endertoolsbox.models.WeatherCondition
import com.example.endertoolsbox.services.LocationService
import com.example.endertoolsbox.services.WeatherService
import com.example.endertoolsbox.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()
    
    private val _currentCity = MutableStateFlow<String>("Chargement...")
    val currentCity: StateFlow<String> = _currentCity.asStateFlow()
    
    private val _lastWeatherUpdateTime = MutableStateFlow<Long>(0L)
    val lastWeatherUpdateTime: StateFlow<Long> = _lastWeatherUpdateTime.asStateFlow()
    
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    private val locationService = LocationService(application.applicationContext)
    private val weatherService = WeatherService()
    private val appContext = application.applicationContext
    
    // Définir les intervalles de mise à jour
    private val timeUpdateIntervalMs = 60_000L  // 1 minute
    private val weatherUpdateIntervalMs = 30 * 60_000L  // 30 minutes
    private val locationUpdateIntervalMs = 5 * 60_000L  // 5 minutes
    private val connectivityCheckIntervalMs = 30_000L // 30 secondes

    init {
        // Mettre à jour l'heure toutes les minutes
        viewModelScope.launch {
            while(true) {
                _currentTime.value = System.currentTimeMillis()
                kotlinx.coroutines.delay(timeUpdateIntervalMs)
            }
        }
        
        // Vérifier périodiquement la connectivité
        viewModelScope.launch {
            while(true) {
                checkConnectivity()
                kotlinx.coroutines.delay(connectivityCheckIntervalMs)
            }
        }

        // Récupérer les données de localisation
        initLocationTracking()
    }
    
    private fun checkConnectivity() {
        val isConnected = NetworkUtils.isNetworkAvailable(appContext)
        if (_isOnline.value != isConnected) {
            _isOnline.value = isConnected
            
            // Si nous revenons en ligne, actualiser les données
            if (isConnected) {
                refreshWeather()
            } else {
                // Si nous passons hors ligne, mettre à jour l'état pour l'afficher
                _weather.value?.let { currentWeather ->
                    _weather.value = currentWeather.copy(isOffline = true)
                }
            }
        }
    }
    
    private fun initLocationTracking() {
        viewModelScope.launch {
            if (locationService.hasLocationPermission()) {
                // Récupérer la dernière position connue pour un démarrage rapide
                locationService.getLastLocation()?.let { location ->
                    _currentLocation.value = location
                    updateCityAndWeather(location)
                }
                
                // Pour éviter trop de mises à jour, nous allons uniquement collecter périodiquement
                viewModelScope.launch {
                    var lastProcessedLocationTime = 0L
                    
                    locationService.getLocationUpdates(intervalMs = locationUpdateIntervalMs / 2)
                        .collectLatest { location ->
                            val now = System.currentTimeMillis()
                            // Ne traiter la nouvelle position que si assez de temps s'est écoulé
                            if (now - lastProcessedLocationTime >= locationUpdateIntervalMs) {
                                _currentLocation.value = location
                                // Seulement mettre à jour la météo si le temps écoulé depuis la dernière mise à jour est suffisant
                                if (now - _lastWeatherUpdateTime.value >= weatherUpdateIntervalMs) {
                                    updateCityAndWeather(location)
                                } else {
                                    // Juste mettre à jour le nom de la ville
                                    _currentCity.value = locationService.getCityName(location.latitude, location.longitude)
                                }
                                lastProcessedLocationTime = now
                            }
                        }
                }
            } else {
                // Fallback avec des données par défaut
                fetchDefaultWeatherData()
            }
        }
    }
    
    private suspend fun updateCityAndWeather(location: Location) {
        val cityName = locationService.getCityName(location.latitude, location.longitude)
        _currentCity.value = cityName
        
        if (_isOnline.value) {
            // Récupérer les données météo basées sur la localisation
            val weather = weatherService.getWeatherForLocation(location, cityName)
            _weather.value = weather
            _lastWeatherUpdateTime.value = System.currentTimeMillis()
        } else {
            // Créer un état hors ligne
            _weather.value = Weather(
                temperature = 10,
                condition = WeatherCondition.CLEAR,
                location = cityName,
                isOffline = true
            )
        }
    }

    private fun fetchDefaultWeatherData() {
        viewModelScope.launch {
            // Données par défaut quand la géolocalisation n'est pas disponible
            kotlinx.coroutines.delay(300) // Simuler un court délai
            
            if (_isOnline.value) {
                _weather.value = Weather(
                    temperature = 10,  // Température par défaut à 10°C comme demandé
                    condition = WeatherCondition.CLEAR,  // Condition par défaut à "Dégagé"
                    location = "Paris"
                )
            } else {
                _weather.value = Weather(
                    temperature = 10,
                    condition = WeatherCondition.CLEAR,
                    location = "Paris",
                    isOffline = true
                )
            }
            _lastWeatherUpdateTime.value = System.currentTimeMillis()
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            checkConnectivity()
            
            if (_isOnline.value) {
                _currentLocation.value?.let { location ->
                    weatherService.invalidateCache() // Forcer le rafraîchissement en invalidant le cache
                    updateCityAndWeather(location)
                } ?: fetchDefaultWeatherData()
            } else {
                // Si nous sommes hors ligne, mettre à jour l'état pour l'afficher
                _weather.value?.let { currentWeather ->
                    if (!currentWeather.isOffline) {
                        _weather.value = currentWeather.copy(isOffline = true)
                    }
                } ?: fetchDefaultWeatherData()
            }
        }
    }
}