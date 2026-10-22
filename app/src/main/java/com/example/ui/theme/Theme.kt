package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FintechDarkColorScheme = darkColorScheme(
    primary = FintechLime,
    onPrimary = FintechLimeDark,
    primaryContainer = FintechLimeSubtle,
    onPrimaryContainer = FintechLimeLight,

    secondary = FintechLimeLight,
    onSecondary = FintechLimeDark,
    secondaryContainer = FintechSurface2,
    onSecondaryContainer = FintechTextPrimary,

    background = FintechBackground,
    onBackground = FintechTextPrimary,

    surface = FintechSurface1,
    onSurface = FintechTextPrimary,
    surfaceVariant = FintechSurface2,
    onSurfaceVariant = FintechTextSecondary,

    outline = FintechBorder,
    outlineVariant = FintechBorderSubtle,

    error = FintechRed,
    onError = Color.White,
    errorContainer = FintechRedSubtle,
    onErrorContainer = FintechRed
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FintechDarkColorScheme,
        typography = FintechTypography,
        content = content
    )
}
