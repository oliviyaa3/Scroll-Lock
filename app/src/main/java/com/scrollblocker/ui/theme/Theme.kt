package com.scrollblocker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Map your new colors to the global Material Theme!
private val NoScrollColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    onPrimary = BackgroundDark,

    secondary = SecondaryPink,
    onSecondary = BackgroundDark,

    background = BackgroundDark,
    onBackground = TextWhite,

    surface = SurfaceDark,
    onSurface = TextWhite,

    surfaceVariant = CardDark,
    onSurfaceVariant = TextGray,

    error = WarningOrange,
    onError = TextWhite,

    outline = TextGray
)

@Composable
fun NoScrollTheme(
    // We force this to true because the new app design is strictly Dark/Neon!
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep false to enforce your custom branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NoScrollColorScheme,
        typography = Typography,
        content = content
    )
}
