package com.pocketos.system

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.runtime.snapshotFlow
import com.pocketos.core.display.Diag
import com.pocketos.core.display.externalDisplay
import com.pocketos.core.input.Pointer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private class CursorView(ctx: Context) : View(ctx) {
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val edge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 3f
    }
    override fun onDraw(canvas: Canvas) {
        val x = Pointer.x
        val y = Pointer.y
        val s = 38f
        val p = Path().apply {
            moveTo(x, y)
            lineTo(x, y + s * 0.85f)
            lineTo(x + s * 0.22f, y + s * 0.65f)
            lineTo(x + s * 0.38f, y + s * 0.98f)
            lineTo(x + s * 0.52f, y + s * 0.92f)
            lineTo(x + s * 0.36f, y + s * 0.6f)
            lineTo(x + s * 0.68f, y + s * 0.6f)
            close()
        }
        canvas.drawPath(p, fill)
        canvas.drawPath(p, edge)
    }
}

class PocketAccessibilityService : AccessibilityService() {
    companion object { @Volatile var instance: PocketAccessibilityService? = null }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var overlay: View? = null
    private var overlayWm: WindowManager? = null

    override fun onServiceConnected() {
        instance = this
        Diag.d("Accesibilidad conectada")
        scope.launch { snapshotFlow { Pointer.active }.collect { if (it) showOverlay() else hideOverlay() } }
        scope.launch { snapshotFlow { Pointer.x to Pointer.y }.collect { overlay?.invalidate() } }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        hideOverlay()
        scope.cancel()
        return super.onUnbind(intent)
    }

    private fun showOverlay() {
        if (overlay != null) return
        val d = externalDisplay(this) ?: return
        try {
            val ctx = createDisplayContext(d).createWindowContext(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null)
            val wm = ctx.getSystemService(WindowManager::class.java)
            val v = CursorView(ctx)
            val lp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT,
            )
            wm.addView(v, lp)
            overlay = v; overlayWm = wm
            Pointer.overlayOn = true
            Diag.d("Cursor superpuesto activo")
        } catch (e: Exception) {
            Diag.d("Cursor superpuesto falló: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun hideOverlay() {
        try { overlay?.let { overlayWm?.removeView(it) } } catch (_: Exception) {}
        overlay = null; overlayWm = null
        Pointer.overlayOn = false
    }

    private fun dispatch(path: Path, durationMs: Long, displayId: Int) {
        val b = GestureDescription.Builder()
        if (Build.VERSION.SDK_INT >= 32) b.setDisplayId(displayId)
        b.addStroke(GestureDescription.StrokeDescription(path, 0, durationMs))
        dispatchGesture(b.build(), null, null)
    }

    fun tap(x: Float, y: Float, displayId: Int) {
        dispatch(Path().apply { moveTo(x, y) }, 50, displayId)
    }

    fun swipe(x1: Float, y1: Float, x2: Float, y2: Float, displayId: Int, ms: Long) {
        dispatch(Path().apply { moveTo(x1, y1); lineTo(x2, y2) }, ms, displayId)
    }

    /** Escribe en el campo con foco de cualquier pantalla. */
    fun typeText(text: String, backspace: Boolean): Boolean {
        val all = windowsOnAllDisplays
        for (i in 0 until all.size()) {
            for (w in all.valueAt(i)) {
                val root = w.root ?: continue
                val f = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT) ?: continue
                if (!f.isEditable) continue
                val cur = if (f.isShowingHintText) "" else (f.text?.toString() ?: "")
                val next = if (backspace) cur.dropLast(1) else cur + text
                val args = Bundle().apply {
                    putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, next)
                }
                return f.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
            }
        }
        return false
    }
}
