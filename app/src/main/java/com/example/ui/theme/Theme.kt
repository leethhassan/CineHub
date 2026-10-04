package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CineHubColorScheme = darkColorScheme(
    primary = CineHubPrimary,
    onPrimary = CineHubTextPrimary,
    primaryContainer = CineHubPrimaryVariant,
    onPrimaryContainer = CineHubTextPrimary,
    secondary = CineHubSecondary,
    onSecondary = CineHubDarkBackground,
    tertiary = CineHubAccent,
    background = CineHubDarkBackground,
    onBackground = CineHubTextPrimary,
    surface = CineHubDarkSurface,
    onSurface = CineHubTextPrimary,
    surfaceVariant = CineHubSurfaceVariant,
    onSurfaceVariant = CineHubTextSecondary,
    outline = CineHubBorder,
    error = CineHubError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark cinematic theme for streaming experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = CineHubColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CineHubDarkBackground.toArgb()
                window.navigationBarColor = CineHubDarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
