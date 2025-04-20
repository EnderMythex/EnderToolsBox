package com.example.endertoolsbox.ui

import android.hardware.ConsumerIrManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.endertoolsbox.models.Screen
import com.example.endertoolsbox.ui.geminichat.GeminiChatScreen
import com.example.endertoolsbox.ui.home.HomeScreen
import com.example.endertoolsbox.ui.irsender.IrSenderScreen
import com.example.endertoolsbox.ui.subscriptions.SubscriptionsScreen
import com.example.endertoolsbox.viewmodels.GeminiChatViewModel
import com.example.endertoolsbox.viewmodels.HomeViewModel
import com.example.endertoolsbox.viewmodels.SubscriptionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    irManager: ConsumerIrManager,
    subscriptionViewModel: SubscriptionViewModel = viewModel(),
    geminiChatViewModel: GeminiChatViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedScreen by remember { mutableStateOf(Screen.Home) }
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                modifier = Modifier
                    .navigationBarsPadding()
                    .width(280.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // En-tête du drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Ender Tools Box",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Version 1.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DrawerNavigationItem(
                    icon = Icons.Default.Home,
                    label = "Accueil",
                    selected = selectedScreen == Screen.Home,
                    onClick = {
                        scope.launch {
                            selectedScreen = Screen.Home
                            drawerState.close()
                        }
                    }
                )

                DrawerNavigationItem(
                    icon = Icons.Default.List,
                    label = "Abonnements",
                    selected = selectedScreen == Screen.Subscriptions,
                    onClick = {
                        scope.launch {
                            selectedScreen = Screen.Subscriptions
                            drawerState.close()
                        }
                    }
                )

                DrawerNavigationItem(
                    icon = Icons.Default.Send,
                    label = "IR Sender",
                    selected = selectedScreen == Screen.IrSender,
                    onClick = {
                        scope.launch {
                            selectedScreen = Screen.IrSender
                            drawerState.close()
                        }
                    }
                )

                DrawerNavigationItem(
                    icon = Icons.Default.Email,
                    label = "Gemini AI Chat",
                    selected = selectedScreen == Screen.GeminiChat,
                    onClick = {
                        scope.launch {
                            selectedScreen = Screen.GeminiChat
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedScreen) {
                Screen.Home -> {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }
                Screen.Subscriptions -> {
                    SubscriptionsScreen(
                        viewModel = subscriptionViewModel,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }
                Screen.IrSender -> {
                    IrSenderScreen(
                        irManager = irManager,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }
                Screen.GeminiChat -> {
                    GeminiChatScreen(
                        viewModel = geminiChatViewModel,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerNavigationItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}