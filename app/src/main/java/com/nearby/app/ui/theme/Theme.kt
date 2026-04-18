package com.nearby.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NearbyColorScheme = darkColorScheme(
    primary          = NearbyCyan,
    onPrimary        = NearbyBlack,
    primaryContainer = NearbyCyanDark,
    secondary        = NearbyGreen,
    onSecondary      = NearbyBlack,
    tertiary         = NearbyYellow,
    background       = NearbyBackground,
    onBackground     = NearbyTextPrimary,
    surface          = NearbySurface,
    onSurface        = NearbyTextPrimary,
    surfaceVariant   = NearbySurfaceLight,
    onSurfaceVariant = NearbyTextSecondary,
    error            = NearbyError,
    outline          = NearbyDivider,
)

@Composable
fun NearbyTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = NearbyBlack.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = NearbyColorScheme,
        typography  = Typography,
        content     = content
    )
}