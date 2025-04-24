package com.example.endertoolsbox.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.endertoolsbox.services.LocationService
import com.example.endertoolsbox.services.WeatherService
import com.example.endertoolsbox.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(private val application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()
    
    private val _loadingProgress = MutableStateFlow(0f)
    val loadingProgress: StateFlow<Float> = _loadingProgress.asStateFlow()
    
    private val _loadingText = MutableStateFlow("Initialisation...")
    val loadingText: StateFlow<String> = _loadingText.asStateFlow()
    
    private val locationService by lazy { LocationService(application.applicationContext) }
    private val weatherService by lazy { WeatherService() }

    init {
        // Démarrer immédiatement le chargement
        viewModelScope.launch(Dispatchers.IO) {
            initializeApp()
        }
    }
    
    private suspend fun initializeApp() = withContext(Dispatchers.IO) {
        try {
            // Initialisation des services de base
            updateLoadingState(0.1f, "Démarrage des services...")
            val networkAvailable = NetworkUtils.isNetworkAvailable(application.applicationContext)
            
            // Vérification des permissions de localisation
            updateLoadingState(0.3f, "Chargement des services de localisation...")
            val locationPermissionGranted = locationService.hasLocationPermission()
            
            if (locationPermissionGranted) {
                // Récupération de la position actuelle
                updateLoadingState(0.5f, "Détermination de votre position...")
                val location = locationService.getLastLocation()
                
                if (location != null && networkAvailable) {
                    // Chargement des données météo
                    updateLoadingState(0.7f, "Récupération des données météo...")
                    val cityName = locationService.getCityName(location.latitude, location.longitude)
                    weatherService.getWeatherForLocation(location, cityName)
                }
            }
            
            // Finalisation
            updateLoadingState(1.0f, "Prêt !")
            
            // Application prête
            _isLoading.value = false
            _isReady.value = true
        } catch (e: Exception) {
            // En cas d'erreur, on considère que l'application est prête quand même
            // pour éviter un blocage au démarrage
            _isLoading.value = false
            _isReady.value = true
        }
    }
    
    private fun updateLoadingState(progress: Float, text: String) {
        _loadingProgress.value = progress
        _loadingText.value = text
    }
    
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
                return SplashViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 