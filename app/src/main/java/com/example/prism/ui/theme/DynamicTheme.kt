package com.example.prism.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.prism.domain.model.Interest


private val ExploringColorScheme = darkColorScheme(
    primary = Color(0xFF7C3AED),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2D1B69),
    onPrimaryContainer = Color(0xFFA78BFA),
    secondary = Color(0xFF06B6D4),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0E3A42),
    onSecondaryContainer = Color(0xFF67E8F9),
    background = Color(0xFF0F0F1A),
    onBackground = Color(0xFFEEEEFF),
    surface = Color(0xFF0F0F1A),
    onSurface = Color(0xFFEEEEFF),
    surfaceVariant = Color(0xFF1E1B33),
    onSurfaceVariant = Color(0xFFBBB8D4),
    outline = Color(0xFF4A4470),
    outlineVariant = Color(0xFF2E2B4A),
    tertiary = Color(0xFFF59E0B),
    error = Color(0xFFF43F5E)
)


private val NewsColorScheme = darkColorScheme(
    primary = Color(0xFFE8A020),
    onPrimary = Color(0xFF1A0E00),
    primaryContainer = Color(0xFF3D2600),
    onPrimaryContainer = Color(0xFFFFD070),
    secondary = Color(0xFFB08050),
    onSecondary = Color(0xFF1A0E00),
    secondaryContainer = Color(0xFF2A1800),
    onSecondaryContainer = Color(0xFFE8C090),
    background = Color(0xFF1A0E00),
    onBackground = Color(0xFFFFF3DC),
    surface = Color(0xFF1A0E00),
    onSurface = Color(0xFFFFF3DC),
    surfaceVariant = Color(0xFF2A1A00),
    onSurfaceVariant = Color(0xFFD4A870),
    outline = Color(0xFF6B4A20),
    outlineVariant = Color(0xFF3D2800),
    tertiary = Color(0xFF8BC34A),
    error = Color(0xFFEF5350)
)

private val SportsColorScheme = darkColorScheme(
    primary = Color(0xFF00E676),
    onPrimary = Color(0xFF001A0A),
    primaryContainer = Color(0xFF00330F),
    onPrimaryContainer = Color(0xFF69FF80),
    secondary = Color(0xFF76FF03),
    onSecondary = Color(0xFF001A0A),
    secondaryContainer = Color(0xFF1A3300),
    onSecondaryContainer = Color(0xFFCCFF90),
    background = Color(0xFF001A0A),
    onBackground = Color(0xFFE8FFF0),
    surface = Color(0xFF001A0A),
    onSurface = Color(0xFFE8FFF0),
    surfaceVariant = Color(0xFF00280F),
    onSurfaceVariant = Color(0xFF80E880),
    outline = Color(0xFF1A6B30),
    outlineVariant = Color(0xFF003318),
    tertiary = Color(0xFFFFD740),
    error = Color(0xFFFF5252)
)

private val FoodColorScheme = darkColorScheme(
    primary = Color(0xFFFF6B35),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF5C1500),
    onPrimaryContainer = Color(0xFFFFB59A),
    secondary = Color(0xFFFFAB40),
    onSecondary = Color(0xFF1A0005),
    secondaryContainer = Color(0xFF4A1500),
    onSecondaryContainer = Color(0xFFFFD9B3),
    background = Color(0xFF1A0005),
    onBackground = Color(0xFFFFEEE8),
    surface = Color(0xFF1A0005),
    onSurface = Color(0xFFFFEEE8),
    surfaceVariant = Color(0xFF2A0A0A),
    onSurfaceVariant = Color(0xFFD4907A),
    outline = Color(0xFF8B2020),
    outlineVariant = Color(0xFF4A0A0A),
    tertiary = Color(0xFF66BB6A),
    error = Color(0xFFEF5350)
)

fun colorSchemeForInterest(interest: Interest) = when (interest) {
    Interest.NONE -> ExploringColorScheme
    Interest.NEWS -> NewsColorScheme
    Interest.SPORTS -> SportsColorScheme
    Interest.FOOD -> FoodColorScheme
}

@Composable
fun PrismDynamicTheme(
    interest: Interest,
    content: @Composable () -> Unit
) {
    val targetScheme = colorSchemeForInterest(interest)
    val animSpec = tween<Color>(durationMillis = 800)

    val primary by animateColorAsState(targetScheme.primary, animSpec, label = "primary")
    val onPrimary by animateColorAsState(targetScheme.onPrimary, animSpec, label = "onPrimary")
    val primaryContainer by animateColorAsState(targetScheme.primaryContainer, animSpec, label = "primaryContainer")
    val onPrimaryContainer by animateColorAsState(targetScheme.onPrimaryContainer, animSpec, label = "onPrimaryContainer")
    val secondary by animateColorAsState(targetScheme.secondary, animSpec, label = "secondary")
    val onSecondary by animateColorAsState(targetScheme.onSecondary, animSpec, label = "onSecondary")
    val secondaryContainer by animateColorAsState(targetScheme.secondaryContainer, animSpec, label = "secondaryContainer")
    val onSecondaryContainer by animateColorAsState(targetScheme.onSecondaryContainer, animSpec, label = "onSecondaryContainer")
    val background by animateColorAsState(targetScheme.background, animSpec, label = "background")
    val onBackground by animateColorAsState(targetScheme.onBackground, animSpec, label = "onBackground")
    val surface by animateColorAsState(targetScheme.surface, animSpec, label = "surface")
    val onSurface by animateColorAsState(targetScheme.onSurface, animSpec, label = "onSurface")
    val surfaceVariant by animateColorAsState(targetScheme.surfaceVariant, animSpec, label = "surfaceVariant")
    val onSurfaceVariant by animateColorAsState(targetScheme.onSurfaceVariant, animSpec, label = "onSurfaceVariant")
    val outline by animateColorAsState(targetScheme.outline, animSpec, label = "outline")
    val outlineVariant by animateColorAsState(targetScheme.outlineVariant, animSpec, label = "outlineVariant")
    val tertiary by animateColorAsState(targetScheme.tertiary, animSpec, label = "tertiary")
    val error by animateColorAsState(targetScheme.error, animSpec, label = "error")

    val animatedScheme = darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        outlineVariant = outlineVariant,
        tertiary = tertiary,
        error = error
    )

    MaterialTheme(
        colorScheme = animatedScheme,
        typography = PrismTypography,
        content = content
    )
}