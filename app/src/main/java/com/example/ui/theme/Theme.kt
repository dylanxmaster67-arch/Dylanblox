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
    primary = DylanGold,
    onPrimary = Color.Black,
    primaryContainer = ElectricPurple,
    onPrimaryContainer = Color.White,
    secondary = CyberCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D5A),
    onSecondaryContainer = CyberCyan,
    tertiary = EmeraldGreen,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = DangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DylanGoldVariant,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFFECB3),
    onPrimaryContainer = Color(0xFF3E2723),
    secondary = Color(0xFF0091EA),
    onSecondary = Color.White,
    tertiary = Color(0xFF00C853),
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun DylanbloxTheme(
    darkTheme: Boolean = true, // Default to sleek gamer dark theme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
