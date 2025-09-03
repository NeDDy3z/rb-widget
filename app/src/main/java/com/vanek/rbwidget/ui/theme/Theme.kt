package com.vanek.rbwidget.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Yellow,
    secondary = Grey,
    tertiary = White,

    background = DarkGrey,

    // Button
    primaryContainer = Yellow,
    secondaryContainer = Yellow,
)


@Composable
fun RBWidgetTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}