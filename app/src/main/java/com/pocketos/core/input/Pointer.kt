package com.pocketos.core.input

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableSharedFlow
import java.lang.ref.WeakReference

/** Puntero compartido: el touchpad del teléfono lo mueve, el escritorio de la TV lo dibuja. */
object Pointer {
    var x by mutableFloatStateOf(400f)
    var y by mutableFloatStateOf(300f)
    var w by mutableFloatStateOf(1920f)
    var h by mutableFloatStateOf(1080f)
    var sens by mutableFloatStateOf(2.5f)
    var active by mutableStateOf(false)
    private var view: WeakReference<View>? = null
    private var downTime = 0L

    fun attach(v: View) { view = WeakReference(v); active = true }
    fun detach(v: View) { if (view?.get() === v) { view = null; active = false } }

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

    fun down() = touch(MotionEvent.ACTION_DOWN)
    fun dragMove() = touch(MotionEvent.ACTION_MOVE)
    fun up() = touch(MotionEvent.ACTION_UP)
    fun click() { down(); up() }

    fun scroll(amount: Float) {
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
}

/** Comandos de los botones del teléfono hacia el escritorio: menu, back, home, switch. */
object Remote {
    val cmds = MutableSharedFlow<String>(extraBufferCapacity = 8)
    fun send(cmd: String) { cmds.tryEmit(cmd) }
}
