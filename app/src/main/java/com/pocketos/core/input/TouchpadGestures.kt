package com.pocketos.core.input

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange

/**
 * 1 dedo: mover. Toque: clic. Mantener y arrastrar: arrastrar.
 * 2 dedos: desplazar (scroll).
 */
fun Modifier.touchpad(): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false)
        var total = Offset.Zero
        val r: String? = withTimeoutOrNull(450L) {
            while (true) {
                val ev = awaitPointerEvent()
                val pressed = ev.changes.count { it.pressed }
                if (pressed == 0) return@withTimeoutOrNull "up"
                if (pressed >= 2) return@withTimeoutOrNull "two"
                val c = ev.changes.first()
                total += c.positionChange()
                c.consume()
                if (total.getDistance() > 24f) return@withTimeoutOrNull "move"
            }
            @Suppress("UNREACHABLE_CODE")
            ""
        }
        when (r) {
            "up" -> Pointer.click()
            null -> {
                Pointer.down()
                while (true) {
                    val ev = awaitPointerEvent()
                    if (ev.changes.none { it.pressed }) break
                    val c = ev.changes.first()
                    val d = c.positionChange()
                    Pointer.moveBy(d.x, d.y)
                    Pointer.dragMove()
                    c.consume()
                }
                Pointer.up()
            }
            "two" -> {
                while (true) {
                    val ev = awaitPointerEvent()
                    if (ev.changes.none { it.pressed }) break
                    val dy = ev.changes.map { it.positionChange().y }.average().toFloat()
                    Pointer.scroll(dy / 25f)
                    ev.changes.forEach { it.consume() }
                }
                Pointer.scrollDone()
            }
            else -> {
                Pointer.moveBy(total.x, total.y)
                while (true) {
                    val ev = awaitPointerEvent()
                    if (ev.changes.none { it.pressed }) break
                    val c = ev.changes.first()
                    val d = c.positionChange()
                    Pointer.moveBy(d.x, d.y)
                    c.consume()
                }
            }
        }
    }
}
