package com.example.endertoolsbox.ui.geminichat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.endertoolsbox.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

// Style moderne pour les bulles de chat
@Composable
fun ModernChatMessageItem(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    val backgroundColor = if (message.isUser) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)
    }

    val contentColor = if (message.isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isUser) 20.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 20.dp
                    )
                )
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Text(
                text = message.content,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Afficher l'heure du message avec style subtil
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        Text(
            text = formatter.format(Date(message.timestamp)),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

