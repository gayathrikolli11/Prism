package com.example.prism.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PrismBaseColorScheme = darkColorScheme(
    primary = Color(0xFF7C3AED),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2D2F7B),
    onPrimaryContainer = Color(0xFFA78BFA),
    secondary = Color(0xFF06B6D4),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0E3A42),
    onSecondaryContainer = Color(0xFF06B6D4),
    background = Color(0xFF0F0F1A),
    onBackground = Color(0xFFF0F0FF),
    surface = Color(0xFF0F0F1A),
    onSurface = Color(0xFFF0F0FF),
    surfaceVariant = Color(0xFF1C1C2E),
    onSurfaceVariant = Color(0xFFAAABCC),
    outline = Color(0xFF3D3D5C),
    outlineVariant = Color(0xFF2A2A40),
    tertiary = Color(0xFFF59E0B),
    error = Color(0xFFF43F5E)
)

@Composable
fun PrismTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PrismBaseColorScheme,
        typography = PrismTypography,
        content = content
    )
}