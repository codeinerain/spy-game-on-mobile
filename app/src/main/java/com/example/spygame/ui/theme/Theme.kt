package com.example.spygame.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SpyAccent,
    onPrimary = SpyAccentInk,
    primaryContainer = SpyPanel2,
    onPrimaryContainer = SpyText,
    secondary = SpyAmber,
    onSecondary = SpyAccentInk,
    secondaryContainer = SpyPanel2,
    onSecondaryContainer = SpyAmber,
    tertiary = SpyOk,
    onTertiary = SpyAccentInk,
    error = SpyDanger,
    onError = SpyText,
    background = SpyBg,
    onBackground = SpyText,
    surface = SpyPanel,
    onSurface = SpyText,
    surfaceVariant = SpyPanel2,
    onSurfaceVariant = SpyMuted,
    outline = SpyLine
)

@Composable
fun SpyGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Spy game has a signature dark espionage aesthetic
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
