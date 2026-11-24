// app/src/main/java/com/project/bansigo_rider/ui/theme/Theme.kt
package com.project.bansigo_rider.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = RoyalNavy,
    onPrimary = SurfaceLight,
    primaryContainer = RoyalNavyLight,
    onPrimaryContainer = RoyalNavyDark,
    secondary = BansiCrimson,
    onSecondary = SurfaceLight,
    secondaryContainer = BansiCrimsonLight,
    onSecondaryContainer = BansiCrimsonDark,
    tertiary = BansiGreen,
    onTertiary = SurfaceLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = InputBackground,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = RoyalNavyLight,
    onPrimary = RoyalNavyDark,
    primaryContainer = RoyalNavyDark,
    onPrimaryContainer = RoyalNavyLight,
    secondary = BansiCrimsonLight,
    onSecondary = BansiCrimsonDark,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

@Composable
fun Bansigo_riderTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = PlusJakartaSans)
        ) {
            content()
        }
    }
}
