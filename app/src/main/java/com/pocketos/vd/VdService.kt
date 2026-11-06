package com.pocketos.vd

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.hardware.input.InputManager
import android.os.Build
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Surface
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

/**
 * Shizuku user service: runs in a separate process with shell privileges. It creates TRUSTED
 * virtual displays (so any app may be launched on them), starts apps there and injects input.
 */
class VdService(private val base: Context) : IVdService.Stub() {
    private val fake = FakeContext(base)
    private val displays = HashMap<Int, VirtualDisplay>()
    private var err = ""

    private val dm: DisplayManager by lazy {
        val c = DisplayManager::class.java.getDeclaredConstructor(Context::class.java)
        c.isAccessible = true
        c.newInstance(fake)
    }

    private val input: InputManager? by lazy {
        try { base.getSystemService(Context.INPUT_SERVICE) as InputManager }
        catch (_: Throwable) {
            try { InputManager::class.java.getDeclaredMethod("getInstance").invoke(null) as InputManager }
            catch (_: Throwable) { null }
        }
    }
    private val injectMethod by lazy {
        InputManager::class.java.getMethod("injectInputEvent", InputEvent::class.java, Int::class.javaPrimitiveType)
    }
    private val setDisplayId by lazy {
        InputEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
    }

    private fun flags(): Int {
        val PUBLIC = 1; val OWN_CONTENT_ONLY = 1 shl 3; val SUPPORTS_TOUCH = 1 shl 6
        val DESTROY_ON_REMOVAL = 1 shl 8; val TRUSTED = 1 shl 10; val OWN_DISPLAY_GROUP = 1 shl 11
        val ALWAYS_UNLOCKED = 1 shl 12; val TOUCH_FEEDBACK_DISABLED = 1 shl 13
        val OWN_FOCUS = 1 shl 14; val DEVICE_DISPLAY_GROUP = 1 shl 15
        var f = PUBLIC or OWN_CONTENT_ONLY or SUPPORTS_TOUCH or DESTROY_ON_REMOVAL
        if (Build.VERSION.SDK_INT >= 33) f = f or TRUSTED or OWN_DISPLAY_GROUP or ALWAYS_UNLOCKED or TOUCH_FEEDBACK_DISABLED
        if (Build.VERSION.SDK_INT >= 34) f = f or OWN_FOCUS or DEVICE_DISPLAY_GROUP
        return f
    }

    override fun destroy() {
        displays.values.forEach { try { it.release() } catch (_: Throwable) {} }
        displays.clear()
        exitProcess(0)
    }

    override fun createDisplay(surface: Surface?, width: Int, height: Int, dpi: Int, name: String): Int = try {
        val vd = dm.createVirtualDisplay(name, width, height, dpi, surface, flags())
            ?: throw IllegalStateException("createVirtualDisplay returned null")
        val id = vd.display.displayId
        displays[id] = vd
        id
    } catch (t: Throwable) {
        err = "${t.javaClass.simpleName}: ${t.message}"
        -1
    }

    override fun resizeDisplay(displayId: Int, width: Int, height: Int, dpi: Int) {
        try { displays[displayId]?.resize(width, height, dpi) } catch (t: Throwable) { err = "resize: ${t.message}" }
    }

    override fun releaseDisplay(displayId: Int) {
        try { displays.remove(displayId)?.release() } catch (t: Throwable) { err = "release: ${t.message}" }
    }

    override fun startActivity(displayId: Int, component: String): String = try {
        val p = ProcessBuilder(
            "/system/bin/am", "start", "--display", displayId.toString(), "-n", component,
        ).redirectErrorStream(true).start()
        p.waitFor(6, TimeUnit.SECONDS)
        p.inputStream.bufferedReader().readText().trim()
    } catch (t: Throwable) { "startActivity failed: ${t.message}" }

    override fun lastError(): String = err

    override fun injectMotion(displayId: Int, event: MotionEvent) {
        try {
            setDisplayId.invoke(event, displayId)
            injectMethod.invoke(input, event, 0)
        } catch (t: Throwable) { err = "motion: ${t.message}" } finally { event.recycle() }
    }

    override fun injectKey(displayId: Int, event: KeyEvent) {
        try {
            setDisplayId.invoke(event, displayId)
            injectMethod.invoke(input, event, 0)
        } catch (t: Throwable) { err = "key: ${t.message}" }
    }
}
