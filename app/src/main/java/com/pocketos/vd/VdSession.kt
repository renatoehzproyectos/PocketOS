package com.pocketos.vd

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import com.pocketos.core.display.Diag

/**
 * One Android app hosted in a PocketOS window: a TextureView whose Surface receives a
 * virtual display, with touch/scroll/keys forwarded into that display.
 */
class VdSession(private val ctx: Context, private val pkg: String, private val name: String, private val dpi: Int) {
    @Volatile var displayId = -1
    var hidden = false
    var onFocus: () -> Unit = {}

    private val main = Handler(Looper.getMainLooper())
    private var surface: Surface? = null
    private var texW = 0
    private var texH = 0
    private var starting = false
    private var resizeRun: Runnable? = null

    private fun log(msg: String) { main.post { Diag.d(msg) } }

    fun createView(c: Context): TextureView = TextureView(c).apply {
        isOpaque = true
        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {
                st.setDefaultBufferSize(w, h)
                texW = w; texH = h
                surface = Surface(st)
                tryStart()
            }
            override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) {
                st.setDefaultBufferSize(w, h)
                texW = w; texH = h
                resizeRun?.let { main.removeCallbacks(it) }
                val r = Runnable {
                    val id = displayId; val svc = VdHost.service
                    if (id >= 0 && svc != null) VdHost.io.execute { try { svc.resizeDisplay(id, w, h, dpi) } catch (_: Throwable) {} }
                }
                resizeRun = r
                main.postDelayed(r, 150)
            }
            override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean { release(); return true }
            override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
        }
        setOnTouchListener { _, ev ->
            if (ev.actionMasked == MotionEvent.ACTION_DOWN) onFocus()
            val id = displayId; val svc = VdHost.service
            if (id >= 0 && svc != null) try { svc.injectMotion(id, ev) } catch (_: Throwable) {}
            true
        }
        setOnGenericMotionListener { _, ev ->
            val id = displayId; val svc = VdHost.service
            if (id >= 0 && svc != null) { try { svc.injectMotion(id, ev) } catch (_: Throwable) {}; true } else false
        }
    }

    /** Creates the virtual display and launches the app once both the Surface and the service exist. */
    fun tryStart() {
        val svc = VdHost.service ?: return
        val s = surface ?: return
        if (starting || displayId >= 0 || texW <= 0 || texH <= 0) return
        starting = true
        val w = texW and 1.inv(); val h = texH and 1.inv()
        VdHost.io.execute {
            try {
                val id = svc.createDisplay(s, w, h, dpi, "PocketOS-$name")
                if (id < 0) { log("VD: could not create display: ${svc.lastError()}"); return@execute }
                displayId = id
                val comp = ctx.packageManager.getLaunchIntentForPackage(pkg)?.component?.flattenToShortString()
                if (comp == null) { log("VD: no launcher activity for $pkg"); return@execute }
                log("VD: display $id ${w}x$h dpi=$dpi -> $comp")
                log("VD: " + svc.startActivity(id, comp).lines().lastOrNull { it.isNotBlank() })
            } catch (t: Throwable) {
                log("VD: ${t.javaClass.simpleName}: ${t.message}")
            } finally { starting = false }
        }
    }

    fun sendKey(e: KeyEvent): Boolean {
        val id = displayId; val svc = VdHost.service ?: return false
        if (id < 0) return false
        return try { svc.injectKey(id, e); true } catch (_: Throwable) { false }
    }

    fun release() {
        val id = displayId; displayId = -1
        val s = surface; surface = null
        val svc = VdHost.service
        VdHost.io.execute {
            if (id >= 0 && svc != null) try { svc.releaseDisplay(id) } catch (_: Throwable) {}
            try { s?.release() } catch (_: Throwable) {}
        }
    }
}
