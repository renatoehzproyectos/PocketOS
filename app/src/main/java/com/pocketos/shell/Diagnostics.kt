package com.pocketos.shell

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.view.Display
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.unit.dp
import com.pocketos.core.display.Diag
import com.pocketos.core.input.Pointer
import com.pocketos.system.PocketAccessibilityService

fun buildReport(ctx: Context): String {
    val dm = ctx.getSystemService(DisplayManager::class.java)
    val sb = StringBuilder()
    sb.appendLine("PocketOS 0.3.0 · ${Build.MANUFACTURER} ${Build.MODEL}")
    sb.appendLine("Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
    sb.appendLine("--- pantallas ---")
    dm.displays.forEach { d ->
        val m = d.mode
        sb.appendLine(
            "id=${d.displayId} '${d.name}' ${m.physicalWidth}x${m.physicalHeight} " +
                "estado=${d.state} flags=0x${Integer.toHexString(d.flags)} " +
                "presentacion=${(d.flags and Display.FLAG_PRESENTATION) != 0} valida=${d.isValid}"
        )
    }
    sb.appendLine("--- estado ---")
    sb.appendLine("Escritorio externo activo: ${Pointer.active}")
    sb.appendLine("Escritorio al frente: ${Pointer.desktopFocused}")
    sb.appendLine("Accesibilidad: ${PocketAccessibilityService.instance != null}")
    sb.appendLine("Cursor superpuesto: ${Pointer.overlayOn}")
    sb.appendLine("Shizuku: ${com.pocketos.vd.VdHost.status}")
    sb.appendLine("--- registro ---")
    Diag.log.forEach { sb.appendLine(it) }
    return sb.toString()
}

@Composable
fun DiagDialog(onClose: () -> Unit) {
    val ctx = LocalContext.current
    val report = buildReport(ctx)
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Diagnóstico") },
        text = {
            Text(
                report, fontFamily = FontFamily.Monospace, fontSize = 10.sp,
                modifier = Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState()),
            )
        },
        confirmButton = {
            TextButton({
                ctx.getSystemService(ClipboardManager::class.java)
                    .setPrimaryClip(ClipData.newPlainText("PocketOS", report))
            }) { Text("Copiar") }
        },
        dismissButton = { TextButton(onClose) { Text("Cerrar") } },
    )
}
