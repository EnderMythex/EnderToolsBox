package com.example.endertoolsbox.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.endertoolsbox.models.WeatherCondition
import com.example.endertoolsbox.utils.capitalizeFirst
import com.example.endertoolsbox.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMenuClick: () -> Unit
) {
    val currentTime by viewModel.currentTime.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val currentCity by viewModel.currentCity.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    // Déterminer le moment de la journée
    val calendar = Calendar.getInstance().apply { timeInMillis = currentTime }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    val greeting = when {
        hour < 5 -> "Bonne nuit"
        hour < 12 -> "Bonjour"
        hour < 18 -> "Bon après-midi"
        else -> "Bonne soirée"
    }

    val dateFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE)
    val formattedDate = dateFormat.format(Date(currentTime))

    val weatherIcon = when {
        weather?.isOffline == true -> Icons.Default.CloudOff
        weather?.condition == WeatherCondition.CLEAR -> Icons.Default.WbSunny
        weather?.condition == WeatherCondition.CLOUDY -> Icons.Default.Cloud
        weather?.condition == WeatherCondition.RAINY -> Icons.Default.Opacity
        weather?.condition == WeatherCondition.STORM -> Icons.Default.Thunderstorm
        else -> Icons.Default.HourglassEmpty
    }

    val weatherText = when {
        weather?.isOffline == true -> "Hors ligne"
        weather?.condition == WeatherCondition.CLEAR -> "Dégagé"
        weather?.condition == WeatherCondition.CLOUDY -> "Nuageux"
        weather?.condition == WeatherCondition.RAINY -> "Pluvieux"
        weather?.condition == WeatherCondition.STORM -> "Orageux"
        else -> "Chargement..."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Accueil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Carte de bienvenue animée
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = formattedDate.capitalizeFirst(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Carte météo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Météo",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Indicateur d'état en ligne/hors ligne
                            if (!isOnline) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = "Hors ligne",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            
                            IconButton(
                                onClick = { viewModel.refreshWeather() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualiser",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Emplacement avec icône de localisation
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = currentCity,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                        
                        // Afficher les coordonnées GPS si disponibles
                        if (currentLocation != null) {
                            val coordsText = " (${String.format("%.4f", currentLocation?.latitude)}, ${String.format("%.4f", currentLocation?.longitude)})"
                            Text(
                                text = coordsText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = weatherIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (weather != null) "${weather?.temperature}°C" else "--°C",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (weather?.isOffline == true) 
                                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                    else 
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = weatherText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (weather?.isOffline == true) 
                                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                    else 
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                
                                // Badge hors ligne
                                if (weather?.isOffline == true) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = MaterialTheme.shapes.small,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = "HORS LIGNE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            val lastUpdateTime by viewModel.lastWeatherUpdateTime.collectAsState()
                            val updateTimeFormatted = if (lastUpdateTime > 0) {
                                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(lastUpdateTime))
                            } else {
                                "Jamais"
                            }
                            val updateTimeText = "Dernière mise à jour: $updateTimeFormatted"
                            
                            Text(
                                text = updateTimeText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Résumé des fonctionnalités
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Fonctionnalités",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Liste des fonctionnalités
                    FeatureItem(
                        icon = Icons.Default.List,
                        title = "Abonnements",
                        description = "Gérez vos abonnements et suivez leurs coûts"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureItem(
                        icon = Icons.Default.Send,
                        title = "IR Sender",
                        description = "Contrôlez vos appareils à distance via infrarouge"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureItem(
                        icon = Icons.Default.Email,
                        title = "Gemini AI Chat",
                        description = "Discutez avec une IA avancée pour obtenir de l'aide"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Pied de page
            Text(
                text = "Ender Tools Box - Votre boîte à outils polyvalente",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}
