package com.example.endertoolsbox

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.endertoolsbox.ui.MainScreen
import com.example.endertoolsbox.ui.theme.EnderToolsTheme
import com.example.endertoolsbox.viewmodels.HomeViewModel
import com.example.endertoolsbox.viewmodels.SplashViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        
        if (locationGranted) {
            // Les permissions ont été accordées, la géolocalisation s'initialisera automatiquement
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Installer le SplashScreen avant d'appeler super.onCreate
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Configuration du mode plein écran qui utilise l'encoche
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Configuration améliorée pour la gestion du clavier
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            // Comportement d'animation du clavier
            controlsAnimationStyle()
            // S'assurer que le clavier ne masque pas le contenu important
            // Note: La propriété isEnterAnimationSupported est supprimée car non disponible
        }
        
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
                        .statusBarsPadding()
                        .imePadding(), // Ajouter un padding pour gérer correctement le clavier
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Configurer la gestion du clavier avec animation fluide
                    val view = LocalView.current
                    DisposableEffect(view) {
                        val windowInsetsController = WindowInsetsControllerCompat(window, view)
                        windowInsetsController.systemBarsBehavior = 
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        
                        onDispose { }
                    }
                    
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
    
    // Extension pour définir le style d'animation du clavier
    private fun WindowInsetsControllerCompat.controlsAnimationStyle() {
        this.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}