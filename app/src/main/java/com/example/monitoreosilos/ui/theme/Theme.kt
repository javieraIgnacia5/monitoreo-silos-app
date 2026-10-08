package com.example.monitoreosilos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealOnContainer,
    secondary = MustardAccent,
    onSecondary = MustardOnAccent,
    secondaryContainer = MustardContainer,
    onSecondaryContainer = MustardOnAccent,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = TealContainer,
    outline = OutlineLight
)

private val DarkColors = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = TealOnContainer,
    primaryContainer = TealContainerDark,
    secondary = MustardAccent,
    onSecondary = MustardOnAccent,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = TealContainerDark,
    outline = OutlineDark
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun MonitoreoSilosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}