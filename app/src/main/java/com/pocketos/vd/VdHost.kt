package com.pocketos.vd

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.view.KeyEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import rikka.shizuku.Shizuku
import java.util.concurrent.Executors

/** Connection to the Shizuku user service plus a registry of the open hosted-app sessions. */
object VdHost {
    var service by mutableStateOf<IVdService?>(null)
        private set
    var status by mutableStateOf("Shizuku not detected. Install and start Shizuku, then tap Connect.")
        private set
    val ready: Boolean get() = service != null

    val io = Executors.newSingleThreadExecutor()
    val sessions = HashMap<Int, VdSession>()
    var focusedWinId = -1

    private var appCtx: Context? = null
    private var inited = false
    private var bound = false

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            service = IVdService.Stub.asInterface(binder)
            status = "Connected. Apps open inside Plasma-style windows."
            appCtx?.let { c -> com.pocketos.core.display.launchDesktopOnExternal(c, force = true) }
        }
        override fun onServiceDisconnected(name: ComponentName) {
            service = null; bound = false
            status = "Service disconnected. Tap Connect."
        }
    }

    fun init(ctx: Context) {
        if (inited) return
        inited = true
        appCtx = ctx.applicationContext
        try {
            Shizuku.addBinderReceivedListenerSticky(Shizuku.OnBinderReceivedListener { onBinder() })
            Shizuku.addBinderDeadListener(Shizuku.OnBinderDeadListener {
                service = null; bound = false; status = "Shizuku stopped. Start it again, then tap Connect."
            })
            Shizuku.addRequestPermissionResultListener(Shizuku.OnRequestPermissionResultListener { _, result ->
                if (result == PackageManager.PERMISSION_GRANTED) bind() else status = "Permission denied in Shizuku."
            })
        } catch (t: Throwable) { status = "Shizuku unavailable: ${t.message}" }
    }

    private fun granted() = try { Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED } catch (_: Throwable) { false }

    private fun onBinder() {
        if (Shizuku.isPreV11()) { status = "Shizuku is too old. Update it."; return }
        if (granted()) bind() else status = "Shizuku is running. Tap Connect and allow PocketOS."
    }

    /** Called from the Settings page button and when an app is opened while not connected. */
    fun requestPermission() {
        try {
            if (!Shizuku.pingBinder()) { status = "Shizuku is not running. Open Shizuku and start it."; return }
            if (granted()) bind() else Shizuku.requestPermission(4242)
        } catch (t: Throwable) { status = "Shizuku error: ${t.message}" }
    }

    private fun bind() {
        val ctx = appCtx ?: return
        if (bound || service != null) return
        bound = true
        status = "Connecting…"
        try {
            val args = Shizuku.UserServiceArgs(ComponentName(ctx.packageName, VdService::class.java.name))
                .daemon(false).processNameSuffix("vd").debuggable(false).version(1)
            Shizuku.bindUserService(args, conn)
        } catch (t: Throwable) { bound = false; status = "Could not start service: ${t.message}" }
    }

    /** Starts an activity on [displayId] with shell privileges (bypasses the Permission Denial). */
    fun startOnDisplay(displayId: Int, component: String, onResult: (String) -> Unit) {
        val svc = service ?: return
        io.execute {
            val out = try { svc.startActivity(displayId, component).lines().lastOrNull { it.isNotBlank() } ?: "ok" }
            catch (t: Throwable) { "${t.javaClass.simpleName}: ${t.message}" }
            android.os.Handler(android.os.Looper.getMainLooper()).post { onResult(out) }
        }
    }

    /** Physical-keyboard keys go to the focused hosted window instead of the shell. */
    fun forwardKey(e: KeyEvent): Boolean {
        val s = sessions[focusedWinId] ?: return false
        if (s.hidden) return false
        when (e.keyCode) {
            KeyEvent.KEYCODE_HOME, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_APP_SWITCH, KeyEvent.KEYCODE_POWER,
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_MUTE -> return false
        }
        return s.sendKey(e)
    }
}
