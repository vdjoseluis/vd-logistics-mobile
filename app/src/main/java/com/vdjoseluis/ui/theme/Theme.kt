package com.vdjoseluis.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    secondary = Color.Black,
    tertiary = Purple,
    onPrimary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    secondary = Color.White,
    tertiary = Purple,
    onPrimary = Blue
)

@Composable
fun VDLogisticsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
