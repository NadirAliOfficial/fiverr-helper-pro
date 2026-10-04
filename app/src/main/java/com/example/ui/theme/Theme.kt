package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FiverrGreen,
    onPrimary = Color.White,
    primaryContainer = FiverrForest,
    onPrimaryContainer = FiverrMint,
    secondary = FiverrMint,
    onSecondary = Slate900,
    secondaryContainer = Slate800,
    onSecondaryContainer = Slate200,
    tertiary = GoldStar,
    onTertiary = Color.Black,
    background = Color(0xFF0B1320),
    surface = Color(0xFF131D2E),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155),
    error = DangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = FiverrDarkGreen,
    onPrimary = Color.White,
    primaryContainer = FiverrMintLight,
    onPrimaryContainer = FiverrForest,
    secondary = FiverrGreen,
    onSecondary = Color.White,
    secondaryContainer = Slate100,
    onSecondaryContainer = Slate800,
    tertiary = GoldStar,
    onTertiary = Color.White,
    background = Slate50,
    surface = Color.White,
    onBackground = Slate900,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate200,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep branded colors consistent
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
