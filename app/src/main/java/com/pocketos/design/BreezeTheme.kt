package com.pocketos.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

object Breeze {
    val Bg = Color(0xFF1B1E20)
    val Panel = Color(0xFF232629)
    val Surface = Color(0xFF31363B)
    val Border = Color(0xFF4D4D4D)
    val Text = Color(0xFFFCFCFC)
    val Muted = Color(0xFFBDC3C7)
    val Blue = Color(0xFF3DAEE9)
}

val LocalAccent = compositionLocalOf { Breeze.Blue }

val Accents = listOf(0xFF3DAEE9, 0xFF1CDC9A, 0xFFF67400, 0xFFDA4453, 0xFF9B59B6, 0xFFFDBC4B)

val Wallpapers = listOf(
    listOf(Color(0xFFB65FCF), Color(0xFF4B2A7B), Color(0xFFD1417D)),
    listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
    listOf(Color(0xFF232526), Color(0xFF414345), Color(0xFF1B1E20)),
    listOf(Color(0xFF134E5E), Color(0xFF1CDC9A), Color(0xFF0B3D2E)),
)

@Composable
fun PocketTheme(accent: Color, content: @Composable () -> Unit) {
    val scheme = darkColorScheme(
        primary = accent, background = Breeze.Bg, surface = Breeze.Panel,
        onSurface = Breeze.Text, onBackground = Breeze.Text,
        surfaceVariant = Breeze.Surface, onSurfaceVariant = Breeze.Muted,
    )
    CompositionLocalProvider(LocalAccent provides accent) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
