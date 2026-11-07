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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

/** Icons of the floating dock, redrawn as vectors from the reference screenshot. */
enum class DockIconKind { Launcher, Settings, Store, Terminal, Files, Firefox, Dino }

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
            DockIconKind.Store -> store()
            DockIconKind.Terminal -> terminal()
            DockIconKind.Files -> files()
            DockIconKind.Firefox -> firefox()
            DockIconKind.Dino -> dino()
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

// ---- 3. Store: blue "key-cap" squircle with a dotted shield-ring -------------------------------
private fun DrawScope.store() {
    val s = size.minDimension
    halo()
    squircle(Brush.verticalGradient(listOf(Color(0xFF3C97EC), Color(0xFF2B7BD6)))) // lower bevel
    squircle(
        Brush.verticalGradient(listOf(Color(0xFF8DECFF), Color(0xFF4CB4FD), Color(0xFF3FA1F4))),
        height = .86f,
    )
    val white = Color(0xF2FFFFFF)
    val dots = PathEffect.dashPathEffect(floatArrayOf(s * .035f, s * .05f))
    val st = Stroke(s * .05f, cap = StrokeCap.Round, pathEffect = dots)
    val p = Path().apply {
        moveTo(s * .25f, s * .40f)
        arcTo(androidx.compose.ui.geometry.Rect(s * .25f, s * .14f, s * .75f, s * .64f), 180f, 180f, false)
        lineTo(s * .75f, s * .50f)
        lineTo(s * .50f, s * .74f)
        lineTo(s * .25f, s * .50f)
        close()
    }
    drawPath(p, white, style = st)
    drawCircle(white, s * .075f, u(.5f, .33f), style = Stroke(s * .03f))
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

// ---- 5. Files: pale-to-sky-blue folder squircle ------------------------------------------------
private fun DrawScope.files() {
    val s = size.minDimension
    halo()
    squircle(Brush.verticalGradient(listOf(Color(0xFFD6F2FC), Color(0xFF8FD2F0), Color(0xFF3FA6DA))), inset = s * .01f)
    // the little dip in the top edge
    drawRoundRect(Color(0xFF1B3B4A), u(.38f, 0f), Size(s * .24f, s * .055f), CornerRadius(s * .03f))
    drawRoundRect(
        Color(0x33FFFFFF), u(.08f, .08f), Size(s * .84f, s * .30f), CornerRadius(s * .2f),
    )
}

// ---- 6. Firefox-style flame globe -----------------------------------------------------------------
private fun DrawScope.firefox() {
    val s = size.minDimension
    val body = Brush.linearGradient(
        listOf(Color(0xFFFFD83D), Color(0xFFFF9A1F), Color(0xFFF2483D), Color(0xFFE8266A)),
        start = u(.80f, .10f), end = u(.25f, .98f),
    )
    drawCircle(body, s * .43f, u(.50f, .56f))
    // flame tail, top right
    val tail = Path().apply {
        moveTo(s * .58f, s * .08f)
        cubicTo(s * .78f, s * .14f, s * .94f, s * .34f, s * .92f, s * .56f)
        lineTo(s * .76f, s * .50f)
        cubicTo(s * .76f, s * .36f, s * .70f, s * .24f, s * .56f, s * .20f)
        close()
    }
    drawPath(tail, Brush.verticalGradient(listOf(Color(0xFFFFE45C), Color(0xFFFF9A1F))))
    // dark crescent + purple core
    drawCircle(Color(0x66B0245A), s * .26f, u(.50f, .60f))
    drawCircle(
        Brush.linearGradient(listOf(Color(0xFF9B6BF2), Color(0xFF5A3AB8)), start = u(.4f, .4f), end = u(.65f, .8f)),
        s * .21f, u(.52f, .60f),
    )
    drawCircle(Color(0xFFFFD83D), s * .02f, u(.86f, .08f))
}

// ---- 7. Purple squircle with a teal pixel dino ------------------------------------------------------
private fun DrawScope.dino() {
    val s = size.minDimension
    halo()
    squircle(Brush.linearGradient(listOf(Color(0xFF9B58B8), Color(0xFF6A4FA2)), start = u(0f, 0f), end = u(1f, 1f)))
    val pts = listOf(
        .50f to .20f, .78f to .20f, .78f to .38f, .64f to .38f, .64f to .46f, .70f to .46f,
        .70f to .54f, .58f to .54f, .58f to .64f, .64f to .64f, .64f to .80f, .50f to .80f,
        .50f to .70f, .40f to .70f, .40f to .80f, .26f to .80f, .26f to .58f, .34f to .58f,
        .34f to .46f, .42f to .46f, .42f to .30f, .50f to .30f,
    )
    val p = Path().apply {
        moveTo(s * pts[0].first, s * pts[0].second)
        for (i in 1 until pts.size) lineTo(s * pts[i].first, s * pts[i].second)
        close()
    }
    drawPath(p, Brush.verticalGradient(listOf(Color(0xFF4DE8C8), Color(0xFF22C7B0))))
}

// =====================================================================================================
// Tray glyphs (white line icons, sizes measured on the reference)
// =====================================================================================================
enum class TrayGlyph { Sun, Ring, Shield, Volume, Chevron }

private val TrayInk = Color(0xFFF2F2F4)

@Composable
fun TrayIcon(kind: TrayGlyph, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        when (kind) {
            TrayGlyph.Sun -> {
                val c = Offset(w / 2f, h / 2f)
                drawCircle(Color(0xFFEDEDEF), w * .26f, c)
                for (i in 0..7) {
                    val a = Math.PI * i / 4.0
                    val ca = kotlin.math.cos(a).toFloat()
                    val sa = kotlin.math.sin(a).toFloat()
                    drawLine(
                        TrayInk,
                        Offset(c.x + ca * w * .37f, c.y + sa * w * .37f),
                        Offset(c.x + ca * w * .47f, c.y + sa * w * .47f),
                        w * .075f, StrokeCap.Round,
                    )
                }
            }
            TrayGlyph.Ring -> {
                val c = Offset(w / 2f, h / 2f)
                drawCircle(TrayInk, w * .45f, c, style = Stroke(w * .075f))
                for (k in 0..2) rotate(60f * k, c) {
                    drawLine(TrayInk, Offset(c.x, c.y - w * .20f), Offset(c.x, c.y + w * .20f), w * .07f, StrokeCap.Round)
                }
            }
            TrayGlyph.Shield -> {
                val p = Path().apply {
                    moveTo(w * .10f, h * .10f); lineTo(w * .90f, h * .10f); lineTo(w * .90f, h * .55f)
                    cubicTo(w * .90f, h * .80f, w * .62f, h * .92f, w * .50f, h * .97f)
                    cubicTo(w * .38f, h * .92f, w * .10f, h * .80f, w * .10f, h * .55f)
                    close()
                }
                drawPath(p, TrayInk, style = Stroke(w * .085f, join = StrokeJoin.Round))
                val v = Path().apply { moveTo(w * .33f, h * .36f); lineTo(w * .50f, h * .66f); lineTo(w * .67f, h * .36f) }
                drawPath(v, TrayInk, style = Stroke(w * .08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            TrayGlyph.Volume -> {
                val p = Path().apply {
                    moveTo(w * .02f, h * .36f); lineTo(w * .22f, h * .36f); lineTo(w * .46f, h * .08f)
                    lineTo(w * .46f, h * .92f); lineTo(w * .22f, h * .64f); lineTo(w * .02f, h * .64f); close()
                }
                drawPath(p, TrayInk)
                drawArc(
                    TrayInk, -48f, 96f, false, Offset(w * .38f, h * .22f), Size(w * .40f, h * .56f),
                    style = Stroke(w * .07f, cap = StrokeCap.Round),
                )
                drawArc(
                    TrayInk, -52f, 104f, false, Offset(w * .46f, h * .06f), Size(w * .52f, h * .88f),
                    style = Stroke(w * .07f, cap = StrokeCap.Round),
                )
            }
            TrayGlyph.Chevron -> {
                val p = Path().apply { moveTo(w * .06f, h * .92f); lineTo(w * .50f, h * .10f); lineTo(w * .94f, h * .92f) }
                drawPath(p, TrayInk, style = Stroke(h * .22f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}
