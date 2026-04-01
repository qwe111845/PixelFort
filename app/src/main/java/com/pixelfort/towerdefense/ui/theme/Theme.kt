package com.pixelfort.towerdefense.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PixelFortColorScheme = darkColorScheme()

@Composable
fun PixelFortTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PixelFortColorScheme,
        content = content
    )
}
