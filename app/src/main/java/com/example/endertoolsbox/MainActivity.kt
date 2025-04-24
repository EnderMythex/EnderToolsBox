package com.example.endertoolsbox

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.example.endertoolsbox.ui.MainScreen
import com.example.endertoolsbox.ui.theme.EnderToolsTheme
import com.example.endertoolsbox.viewmodels.HomeViewModel
import com.example.endertoolsbox.viewmodels.SplashViewModel

class MainActivity : ComponentActivity() {
    private lateinit var irManager: ConsumerIrManager
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var splashViewModel: SplashViewModel

    // Demande de permissions
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Installer le SplashScreen avant d'appeler super.onCreate
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Configuration du mode plein écran qui utilise l'encoche
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        
        irManager = getSystemService(Context.CONSUMER_IR_SERVICE) as ConsumerIrManager
        
        // Initialiser le SplashViewModel avec la Factory
        splashViewModel = ViewModelProvider(
            this,
            SplashViewModel.Factory(application)
        )[SplashViewModel::class.java]
        
        // Configurer le SplashScreen pour qu'il reste affiché jusqu'à ce que le chargement soit terminé
        splashScreen.setKeepOnScreenCondition {
            !splashViewModel.isReady.value
        }
        
        // Demander les permissions de localisation
        requestLocationPermissions()

        setContent {
            EnderToolsTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(irManager, homeViewModel = homeViewModel)
                }
            }
        }
    }
    
    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}