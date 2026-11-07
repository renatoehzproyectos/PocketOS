package com.pocketos.shell

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/** Icons of the floating dock, redrawn as vectors from the reference screenshot. */
enum class DockIconKind { Launcher, Settings, Terminal, Files, Chrome }

/** Squircle corner radius relative to the icon size (measured: ~26-28 %). */
private const val SquircleR = .27f

private fun DrawScope.u(x: Float, y: Float) = Offset(size.width * x, size.height * y)

/** Soft dark halo visible around every squircle in the reference. */
private fun DrawScope.halo() {
    val s = size.minDimension
    for (i in 1..3) {
        val g = s * .018f * i
        drawRoundRect(
            Color(0x16000000),
            Offset(-g, -g + s * .03f), Size(s + g * 2, s + g * 2),
            CornerRadius(s * SquircleR + g),
        )
    }
}

private fun DrawScope.squircle(brush: Brush, inset: Float = 0f, height: Float = 1f) {
    val s = size.minDimension
    val side = s - inset * 2
    drawRoundRect(brush, Offset(inset, inset), Size(side, side * height), CornerRadius(side * SquircleR))
}

@Composable
fun DockIcon(kind: DockIconKind, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        when (kind) {
            DockIconKind.Launcher -> launcher()
            DockIconKind.Settings -> settings()
            DockIconKind.Terminal -> terminal()
            DockIconKind.Files -> files()
            DockIconKind.Chrome -> chrome()
        }
    }
}

// ---- 1. Launcher: 2x2 grid of rounded cells, two bright / two dim -----------------------------
private fun DrawScope.launcher() {
    val s = size.minDimension
    val cell = s * .405f
    val gap = s * .14f
    val x0 = (size.width - (cell * 2 + gap)) / 2f
    val y0 = (size.height - (cell * 2 + gap)) / 2f
    val rad = CornerRadius(cell * .2f)
    for (row in 0..1) for (col in 0..1) {
        val isBright = (col == 1 && row == 0) || (col == 0 && row == 1)
        val tl = Offset(x0 + col * (cell + gap), y0 + row * (cell + gap))
        drawRoundRect(
            if (isBright) Color(0xFFA8ADB1) else Color(0xFF626867),
            tl, Size(cell, cell), rad,
        )
        drawRoundRect(
            if (isBright) Color(0xFFF4F4F4) else Color(0xFFB4B2B0),
            tl, Size(cell, cell), rad, style = Stroke(s * .055f),
        )
    }
}

// ---- 2. Settings: silver squircle with a ring ----------------------------------------------------
private fun DrawScope.settings() {
    val s = size.minDimension
    halo()
    squircle(Brush.verticalGradient(listOf(Color(0xFFC6CDE2), Color(0xFFA9B4CE), Color(0xFF8F9DB9))))
    squircle(Brush.verticalGradient(listOf(Color(0x66FFFFFF), Color(0x00FFFFFF))), inset = s * .02f, height = .5f)
    val c = u(.5f, .5f)
    drawCircle(Color(0x55FFFFFF), s * .37f, c, style = Stroke(s * .025f))
    drawCircle(Color(0xF2FFFFFF), s * .275f, c, style = Stroke(s * .07f))
    drawCircle(
        Brush.verticalGradient(listOf(Color(0xFF7683A3), Color(0xFF59668A))),
        s * .225f, c,
    )
    // tiny tick marks at 12, 3 and 6 o'clock
    val tick = Color(0xAAFFFFFF)
    drawLine(tick, u(.5f, .10f), u(.5f, .15f), s * .03f, StrokeCap.Round)
    drawLine(tick, u(.5f, .85f), u(.5f, .90f), s * .03f, StrokeCap.Round)
    drawLine(tick, u(.86f, .5f), u(.91f, .5f), s * .03f, StrokeCap.Round)
}

// ---- 4. Terminal: dark squircle with a light rounded border -------------------------------------
private fun DrawScope.terminal() {
    val s = size.minDimension
    halo()
    squircle(Brush.verticalGradient(listOf(Color(0xFFB5B2B1), Color(0xFF8C8A8A))))
    squircle(Brush.verticalGradient(listOf(Color(0xFF2A2E2F), Color(0xFF212526))), inset = s * .065f)
    val ink = Color(0xDDEDEDED)
    val chevron = Path().apply {
        moveTo(s * .21f, s * .30f); lineTo(s * .30f, s * .37f); lineTo(s * .21f, s * .44f)
    }
    drawPath(chevron, ink, style = Stroke(s * .035f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawLine(ink, u(.50f, .47f), u(.64f, .47f), s * .035f, StrokeCap.Round)
}

// ---- Files (Dolphin): pale-to-sky-blue folder squircle ------------------------------------------
private fun DrawScope.files() {
    val s = size.minDimension
    halo()
    squircle(Brush.verticalGradient(listOf(Color(0xFFD6F2FC), Color(0xFF8FD2F0), Color(0xFF3FA6DA))), inset = s * .01f)
    drawRoundRect(Color(0xFF1B3B4A), u(.38f, 0f), Size(s * .24f, s * .055f), CornerRadius(s * .03f))
    drawRoundRect(Color(0x33FFFFFF), u(.08f, .08f), Size(s * .84f, s * .30f), CornerRadius(s * .2f))
}

// ---- Chrome: red / yellow / green wheel with a blue core --------------------------------------------
private fun DrawScope.chrome() {
    val s = size.minDimension
    val tl = u(.05f, .05f)
    val d = Size(s * .90f, s * .90f)
    drawArc(Color(0xFFEA4335), 210f, 120f, true, tl, d)
    drawArc(Color(0xFFFBBC05), 330f, 120f, true, tl, d)
    drawArc(Color(0xFF34A853), 90f, 120f, true, tl, d)
    drawCircle(Color(0xFFFFFFFF), s * .23f, u(.5f, .5f))
    drawCircle(Color(0xFF4285F4), s * .18f, u(.5f, .5f))
}
