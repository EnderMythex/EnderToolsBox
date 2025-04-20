package com.example.endertoolsbox.ui.irsender

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Item de log avec style moderne
@Composable
fun LogItem(log: String) {
    val isError = log.contains("Erreur")
    val backgroundColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    }

    val textColor = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    val (timestamp, message) = log.split(" - ", limit = 2)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}

