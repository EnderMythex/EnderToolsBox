package com.example.endertoolsbox.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EnderToolsTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = Color(0xFF86BFFC),
            onPrimary = Color(0xFF003258),
            primaryContainer = Color(0xFF00497B),
            onPrimaryContainer = Color(0xFFD0E4FF),
            secondary = Color(0xFFBDC7DC),
            onSecondary = Color(0xFF293041),
            secondaryContainer = Color(0xFF3F4759),
            onSecondaryContainer = Color(0xFFD9E3F8),
            tertiary = Color(0xFFDFBCDC),
            onTertiary = Color(0xFF422740),
            tertiaryContainer = Color(0xFF5A3D58),
            onTertiaryContainer = Color(0xFFFCD8F8),
            background = Color(0xFF1A1C1E),
            onBackground = Color(0xFFE2E2E6),
            surface = Color(0xFF121316),
            surfaceVariant = Color(0xFF43474E),
            onSurfaceVariant = Color(0xFFC3C7CF)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF0061A4),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFD1E4FF),
            onPrimaryContainer = Color(0xFF001D36),
            secondary = Color(0xFF535F70),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFD7E3F8),
            onSecondaryContainer = Color(0xFF101C2B),
            tertiary = Color(0xFF72596F),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFCD7F6),
            onTertiaryContainer = Color(0xFF2B1629),
            background = Color(0xFFFDFCFF),
            onBackground = Color(0xFF1A1C1E),
            surface = Color(0xFFFAF9FD),
            surfaceVariant = Color(0xFFDFE2EB),
            onSurfaceVariant = Color(0xFF43474E)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp
            ),
            titleLarge = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp
            ),
            labelSmall = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 0.5.sp
            )
        ),
        shapes = Shapes(
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(24.dp)
        ),
        content = content
    )
}