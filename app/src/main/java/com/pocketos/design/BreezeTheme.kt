package com.pocketos.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.pocketos.R
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope

object Breeze {
    // Neutral graphite palette measured from the reference panel (rgb 44,44,52 base).
    val Bg = Color(0xFF1E1E24)
    val Panel = Color(0xFF2C2C34)
    val Surface = Color(0xFF34343C)
    val Border = Color(0xFF4A4A54)
    val Text = Color(0xFFF2F2F4)
    val Muted = Color(0xFFA6A6B0)
    val Blue = Color(0xFF3B69C7) // workspace pill / accent sampled from the reference
}

/**
 * Dock (taskbar) metrics. Every value is in dp of the 1920 px-wide shell layout and was
 * measured on the 1024 px-wide reference screenshot (x 1.875).
 */
object Dock {
    const val Height = 77f        // 41 px in the reference
    const val BottomGap = 10f     // floating gap between the screen's bottom edge and the dock
    const val IconSize = 47f      // 25 px
    const val Pitch = 85f         // 45.2 px between icon centers
    const val IconsStart = 700f   // left edge of the first slot (launcher)
    const val IconsLift = 2f      // icons sit ~1 px above the bar's centre line
    const val Alpha = 0.82f       // translucency of the panel
}

val LocalAccent = compositionLocalOf { Breeze.Blue }

val Accents = listOf(0xFF3B69C7, 0xFF1CDC9A, 0xFFF67400, 0xFFDA4453, 0xFF9B59B6, 0xFFFDBC4B)

val Wallpapers = listOf(
    listOf(Color(0xFF401071), Color(0xFF6F2B91), Color(0xFFAA387D), Color(0xFFEF7A9B)),
    listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)),
    listOf(Color(0xFF232526), Color(0xFF414345), Color(0xFF1B1E20)),
    listOf(Color(0xFF134E5E), Color(0xFF1CDC9A), Color(0xFF0B3D2E)),
    // 4: Amethyst (fallback gradient; the real image is WallpaperRes[4])
    listOf(Color(0xFF1F5F9E), Color(0xFF16304F), Color(0xFF0B1424)),
)

/** Image wallpapers by index in [Wallpapers]; null entries are drawn as gradients. */
val WallpaperRes: List<Int?> = listOf(
    R.drawable.wallpaper_plasma,
    null,
    null,
    null,
    R.drawable.wallpaper_amethyst,
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
        val noto = FontFamily(
            Font(R.font.noto_sans_regular, FontWeight.Normal),
            Font(R.font.noto_sans_bold, FontWeight.Bold),
        )
        MaterialTheme(colorScheme = scheme) {
            ProvideTextStyle(TextStyle(fontFamily = noto), content)
        }
    }
}
