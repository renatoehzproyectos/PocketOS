package com.pocketos.core.applications

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
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
    AppInfo("Archivos", "pocketos:files", null, "📁"),
    AppInfo("Ajustes", "pocketos:settings", null, "⚙"),
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

fun launchApp(ctx: Context, pkg: String, displayId: Int) {
    val intent = ctx.packageManager.getLaunchIntentForPackage(pkg) ?: return
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        val opts = ActivityOptions.makeBasic().setLaunchDisplayId(displayId)
        ctx.startActivity(intent, opts.toBundle())
    } catch (e: Exception) {
        try { ctx.startActivity(intent) } catch (_: Exception) {}
    }
}
