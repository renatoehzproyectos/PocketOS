package com.pocketos.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope

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
    listOf(Color(0xFF401071), Color(0xFF6F2B91), Color(0xFFAA387D), Color(0xFFEF7A9B)),
    listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
    listOf(Color(0xFF232526), Color(0xFF414345), Color(0xFF1B1E20)),
    listOf(Color(0xFF134E5E), Color(0xFF1CDC9A), Color(0xFF0B3D2E)),
)

/** Full-bleed angular purple Plasma-style wallpaper. Every shape closes to the
 * canvas edges, so no transparent holes or screenshot artifacts can show through. */
fun drawPlasmaWallpaper(drawScope: DrawScope) = with(drawScope) {
    val w = size.width
    val h = size.height
    drawRect(Brush.linearGradient(listOf(Color(0xFF54227F), Color(0xFF35104F), Color(0xFF160D2C))))

    fun ridge(points: List<Pair<Float, Float>>, colors: List<Color>) {
        val p = Path().apply {
            val first = points.first()
            moveTo(w * first.first, h * first.second)
            points.forEach { (x, y) -> lineTo(w * x, h * y) }
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(p, Brush.linearGradient(colors))
    }

    // Deep upper facets and the characteristic zig-zag horizon.
    ridge(listOf(0f to .38f, .17f to .30f, .22f to .38f, .39f to .31f,
        .53f to .23f, .64f to .31f, .81f to .20f, .87f to .28f, 1f to .33f),
        listOf(Color(0xFF773BA8), Color(0xFF3D155F), Color(0xFF24103D)))

    // Middle purple plane, with its own angular upper edge.
    ridge(listOf(0f to .58f, .18f to .51f, .23f to .46f, .34f to .52f,
        .49f to .44f, .62f to .53f, .78f to .43f, .84f to .49f, 1f to .57f),
        listOf(Color(0xFF762477), Color(0xFF54165F), Color(0xFF3B124F)))

    // Lower magenta ridges and the pink foreground.
    ridge(listOf(0f to .79f, .20f to .67f, .31f to .75f, .46f to .63f,
        .61f to .72f, .77f to .61f, .84f to .66f, 1f to .72f),
        listOf(Color(0xFF951D68), Color(0xFF6B185D), Color(0xFF49134F)))
    ridge(listOf(0f to .95f, .20f to .84f, .30f to .91f, .46f to .75f,
        .62f to .85f, .81f to .73f, .87f to .78f, 1f to .84f),
        listOf(Color(0xFFE65F91), Color(0xFFBC397F), Color(0xFF77205F)))
    ridge(listOf(0f to 1f, .30f to .99f, .46f to .85f, .62f to .94f,
        .81f to .82f, .87f to .86f, 1f to .92f),
        listOf(Color(0xFFF27C9A), Color(0xFFE05B8E), Color(0xFFA92F77)))
}

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
