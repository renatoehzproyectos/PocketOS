package com.pocketos.core.input

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.compose.runtime.*
import com.pocketos.system.PocketAccessibilityService
import kotlinx.coroutines.flow.MutableSharedFlow
import java.lang.ref.WeakReference
import kotlin.math.hypot

/**
 * Puntero compartido. Si el escritorio de PocketOS está al frente en la TV, los toques se
 * inyectan en su vista. Si hay otra app encima, se usa el servicio de accesibilidad.
 */
object Pointer {
    var x by mutableFloatStateOf(400f)
    var y by mutableFloatStateOf(300f)
    var w by mutableFloatStateOf(1920f)
    var h by mutableFloatStateOf(1080f)
    var sens by mutableFloatStateOf(2.5f)
    var active by mutableStateOf(false)
    var desktopFocused by mutableStateOf(false)
    var overlayOn by mutableStateOf(false)
    var displayId = 0
    private var view: WeakReference<View>? = null
    private var downTime = 0L
    private var sx = 0f
    private var sy = 0f
    private var a11yDrag = false
    private var scrollAcc = 0f

    fun attach(v: View, display: Int) { view = WeakReference(v); displayId = display; active = true }
    fun detach(v: View) {
        if (view?.get() === v) { view = null; active = false; desktopFocused = false }
    }

    private fun a11y(): PocketAccessibilityService? =
        if (desktopFocused) null else PocketAccessibilityService.instance

    fun moveBy(dx: Float, dy: Float) {
        x = (x + dx * sens).coerceIn(0f, (w - 1f).coerceAtLeast(0f))
        y = (y + dy * sens).coerceIn(0f, (h - 1f).coerceAtLeast(0f))
    }

    private fun touch(action: Int) {
        val v = view?.get() ?: return
        val now = SystemClock.uptimeMillis()
        if (action == MotionEvent.ACTION_DOWN) downTime = now
        val e = MotionEvent.obtain(downTime, now, action, x, y, 0)
        e.source = InputDevice.SOURCE_TOUCHSCREEN
        v.dispatchTouchEvent(e)
        e.recycle()
    }

    fun down() {
        val s = a11y()
        if (s != null) { a11yDrag = true; sx = x; sy = y } else { a11yDrag = false; touch(MotionEvent.ACTION_DOWN) }
    }

    fun dragMove() { if (!a11yDrag) touch(MotionEvent.ACTION_MOVE) }

    fun up() {
        if (a11yDrag) {
            val s = PocketAccessibilityService.instance ?: return
            if (hypot(x - sx, y - sy) < 12f) s.tap(x, y, displayId) else s.swipe(sx, sy, x, y, displayId, 400)
            a11yDrag = false
        } else touch(MotionEvent.ACTION_UP)
    }

    fun click() {
        val s = a11y()
        if (s != null) s.tap(x, y, displayId)
        else { touch(MotionEvent.ACTION_DOWN); touch(MotionEvent.ACTION_UP) }
    }

    fun scroll(amount: Float) {
        if (a11y() != null) { scrollAcc += amount * 25f; return }
        val v = view?.get() ?: return
        val props = arrayOf(MotionEvent.PointerProperties().apply { id = 0; toolType = MotionEvent.TOOL_TYPE_MOUSE })
        val px = x
        val py = y
        val coords = arrayOf(MotionEvent.PointerCoords().apply {
            this.x = px; this.y = py
            setAxisValue(MotionEvent.AXIS_VSCROLL, amount)
        })
        val now = SystemClock.uptimeMillis()
        val e = MotionEvent.obtain(
            now, now, MotionEvent.ACTION_SCROLL, 1, props, coords,
            0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_MOUSE, 0,
        )
        v.dispatchGenericMotionEvent(e)
        e.recycle()
    }

    /** Al soltar los dos dedos: en apps de terceros se hace un único gesto de deslizar. */
    fun scrollDone() {
        val s = PocketAccessibilityService.instance
        if (scrollAcc != 0f && s != null) {
            val y2 = (y + scrollAcc * 1.5f).coerceIn(0f, h - 1f)
            s.swipe(x, y, x, y2, displayId, 250)
        }
        scrollAcc = 0f
    }
}

/** Comandos de los botones del teléfono hacia el escritorio: menu, back, home, switch. */
object Remote {
    val cmds = MutableSharedFlow<String>(extraBufferCapacity = 8)
    fun send(cmd: String) { cmds.tryEmit(cmd) }
}
