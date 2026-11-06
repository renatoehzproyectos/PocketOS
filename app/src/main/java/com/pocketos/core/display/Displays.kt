package com.pocketos.core.display

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.SystemClock
import android.view.Display
import androidx.compose.runtime.mutableStateListOf
import com.pocketos.DesktopActivity
import com.pocketos.core.input.Pointer
import com.pocketos.vd.VdHost
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Registro visible en Diagnóstico para saber qué pasó al detectar la TV. */
object Diag {
    val log = mutableStateListOf<String>()
    fun d(msg: String) {
        val t = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        log.add(0, "$t $msg")
        while (log.size > 40) log.removeAt(log.lastIndex)
    }
}

fun externalDisplay(ctx: Context): Display? =
    ctx.getSystemService(DisplayManager::class.java).displays
        .firstOrNull { it.displayId != Display.DEFAULT_DISPLAY && it.isValid }

private var lastLaunch = 0L
private var attempts = 0
private var gaveUpLogged = false
private var shizukuHinted = false

fun resetLaunchAttempts() { attempts = 0; gaveUpLogged = false }

fun launchDesktopOnExternal(ctx: Context, force: Boolean = false): Boolean {
    val d = externalDisplay(ctx) ?: return false
    if (force) resetLaunchAttempts()
    val now = SystemClock.uptimeMillis()
    if (!force) {
        if (Pointer.active) return true
        if (now - lastLaunch < 2500) return true
        if (attempts >= 8) {
            if (!gaveUpLogged) { Diag.d("Detenido tras 8 intentos. Abre Diagnóstico y envíame el informe."); gaveUpLogged = true }
            return false
        }
    }
    lastLaunch = now
    attempts++
    return try {
        val o = ActivityOptions.makeBasic().setLaunchDisplayId(d.displayId)
        ctx.startActivity(
            Intent(ctx, DesktopActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            o.toBundle(),
        )
        Diag.d("Lanzado en pantalla ${d.displayId} '${d.name}' (intento $attempts)")
        true
    } catch (e: Exception) {
        Diag.d("Error al lanzar: ${e.javaClass.simpleName}: ${e.message?.take(60)}")
        if (e is SecurityException) {
            if (VdHost.ready) {
                val comp = ComponentName(ctx, DesktopActivity::class.java).flattenToShortString()
                VdHost.startOnDisplay(d.displayId, comp) { Diag.d("Shizuku am start: $it") }
                true
            } else {
                if (!shizukuHinted) { Diag.d("Android bloquea el lanzamiento. Menú ⋮ -> Shizuku: conectar."); shizukuHinted = true }
                false
            }
        } else false
    }
}
