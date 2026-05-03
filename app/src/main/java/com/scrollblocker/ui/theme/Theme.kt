package com.scrollblocker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NoScrollColorScheme,
        typography = Typography,
        content = content
    )
}
