package com.pocketos.desktop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pocketos.core.input.Pointer
import kotlin.math.roundToInt

@Composable
fun CursorOverlay() {
    Canvas(Modifier.offset { IntOffset(Pointer.x.roundToInt(), Pointer.y.roundToInt()) }.size(26.dp)) {
        val s = size.width
        val p = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, s * 0.85f)
            lineTo(s * 0.22f, s * 0.65f)
            lineTo(s * 0.38f, s * 0.98f)
            lineTo(s * 0.52f, s * 0.92f)
            lineTo(s * 0.36f, s * 0.6f)
            lineTo(s * 0.68f, s * 0.6f)
            close()
        }
        drawPath(p, Color.White)
        drawPath(p, Color.Black, style = Stroke(2.5f))
    }
}
