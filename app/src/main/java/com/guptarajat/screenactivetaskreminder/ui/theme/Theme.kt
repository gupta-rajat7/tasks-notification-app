package com.guptarajat.screenactivetaskreminder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F675F),
    secondary = Color(0xFF6B5E00),
    tertiary = Color(0xFF735097),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF73D6C9),
    secondary = Color(0xFFD7C765),
    tertiary = Color(0xFFD7B8FF),
)

@Composable
fun TaskReminderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content,
    )
}
