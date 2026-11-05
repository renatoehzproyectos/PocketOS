package com.pocketos.core.applications

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import com.pocketos.core.display.Diag
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

data class AppInfo(
    val label: String,
    val pkg: String,
    val icon: ImageBitmap? = null,
    val glyph: String = "",
)

val Builtins = listOf(
    AppInfo("Dolphin", "pocketos:files", null, "📁"),
    AppInfo("System Settings", "pocketos:settings", null, "⚙"),
)

fun loadApps(ctx: Context): List<AppInfo> {
    val pm = ctx.packageManager
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return pm.queryIntentActivities(intent, 0)
        .filter { it.activityInfo.packageName != ctx.packageName }
        .map {
            AppInfo(
                it.loadLabel(pm).toString(),
                it.activityInfo.packageName,
                it.loadIcon(pm).toBitmap(96, 96).asImageBitmap(),
            )
        }
        .distinctBy { it.pkg }
        .sortedBy { it.label.lowercase() }
}

/**
 * Launches an Android app as a freeform window at [bounds] (pixels on the target display).
 * Requires on the phone: Developer options -> "Enable freeform windows" and
 * "Force activities to be resizable", then reboot.
 */
fun launchApp(ctx: Context, pkg: String, displayId: Int, bounds: Rect? = null) {
    val intent = ctx.packageManager.getLaunchIntentForPackage(pkg) ?: return
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val opts = ActivityOptions.makeBasic().setLaunchDisplayId(displayId)
    if (bounds != null) {
        opts.setLaunchBounds(bounds)
        try {
            // WINDOWING_MODE_FREEFORM = 5 (hidden API, best effort)
            ActivityOptions::class.java.getMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType)
                .invoke(opts, 5)
        } catch (e: Throwable) {
            Diag.d("setLaunchWindowingMode no disponible: ${e.javaClass.simpleName}")
        }
    }
    try {
        ctx.startActivity(intent, opts.toBundle())
        Diag.d("Lanzado $pkg en display $displayId bounds=$bounds")
    } catch (e: Exception) {
        Diag.d("Fallo ventana para $pkg: ${e.message}")
        try { ctx.startActivity(intent) } catch (_: Exception) {}
    }
}
