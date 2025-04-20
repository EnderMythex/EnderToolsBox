package com.example.endertoolsbox.ui.geminichat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.endertoolsbox.models.ChatMessage
import com.example.endertoolsbox.viewmodels.GeminiChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatScreen(
    viewModel: GeminiChatViewModel,
    onMenuClick: () -> Unit
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var userInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Gemini AI Chat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        // Badge "AI Powered"
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "AI Powered",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Effacer la conversation"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { padding ->
        // Fond d'écran du chat avec gradient subtil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Zone des messages
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom)
                ) {
                    items(chatMessages.asReversed()) { message ->
                        ModernChatMessageItem(message = message)
                    }
                }

                // Zone de saisie et bouton d'envoi
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 8.dp,
                                top = 8.dp,
                                bottom = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            placeholder = {
                                Text(
                                    "Posez votre question à Gemini...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                containerColor = androidx.compose.ui.graphics.Color.Transparent
                            ),
                            shape = MaterialTheme.shapes.medium,
                            maxLines = 5
                        )

                        Box(contentAlignment = Alignment.Center) {
                            Button(
                                onClick = {
                                    if (userInput.isNotBlank() && !isLoading) {
                                        viewModel.sendMessage(userInput)
                                        userInput = ""
                                    }
                                },
                                enabled = userInput.isNotBlank() && !isLoading,
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Envoyer",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

