package com.pocketos.core.display

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.SystemClock
import android.view.Display
import com.pocketos.DesktopActivity
import com.pocketos.core.input.Pointer

fun externalDisplay(ctx: Context): Display? =
    ctx.getSystemService(DisplayManager::class.java).displays
        .firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }

private var lastLaunch = 0L

fun launchDesktopOnExternal(ctx: Context, force: Boolean = false): Boolean {
    val d = externalDisplay(ctx) ?: return false
    val now = SystemClock.uptimeMillis()
    if (!force && (Pointer.active || now - lastLaunch < 4000)) return true
    lastLaunch = now
    return try {
        val o = ActivityOptions.makeBasic().setLaunchDisplayId(d.displayId)
        ctx.startActivity(
            Intent(ctx, DesktopActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            o.toBundle(),
        )
        true
    } catch (e: Exception) { false }
}
