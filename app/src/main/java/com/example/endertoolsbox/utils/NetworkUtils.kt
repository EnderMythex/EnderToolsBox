package com.example.endertoolsbox.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Classe utilitaire pour vérifier l'état de la connexion Internet
 */
object NetworkUtils {
    /**
     * Vérifie si l'appareil est connecté à Internet
     *
     * @param context Le contexte de l'application
     * @return true si l'appareil est connecté à Internet, false sinon
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = 
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = 
            connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
} 