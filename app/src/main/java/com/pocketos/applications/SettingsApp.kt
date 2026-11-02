package com.pocketos.applications

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Build
import android.provider.Settings
import android.view.Display
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import com.pocketos.DesktopActivity
import com.pocketos.core.storage.Keys
import com.pocketos.design.Accents
import com.pocketos.design.Breeze
import com.pocketos.design.LocalAccent
import com.pocketos.design.Wallpapers

private fun openSystem(ctx: Context, action: String) {
    try { ctx.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: Exception) {}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsApp(prefs: Preferences, onAccent: (Long) -> Unit, onWall: (Int) -> Unit) {
    val ctx = LocalContext.current
    val accent = LocalAccent.current
    var cat by remember { mutableIntStateOf(0) }
    var tick by remember { mutableIntStateOf(0) }
    val cats = listOf("Apariencia", "Pantallas", "Sistema")
    val curAccent = prefs[Keys.ACCENT] ?: Accents[0]
    val curWall = prefs[Keys.WALL] ?: 0

    Row(Modifier.fillMaxSize()) {
        Column(Modifier.width(120.dp).fillMaxHeight().background(Breeze.Panel).padding(6.dp)) {
            cats.forEachIndexed { i, c ->
                Text(
                    c, color = Breeze.Text, fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp))
                        .background(if (i == cat) accent.copy(alpha = 0.3f) else Color.Transparent)
                        .clickable { cat = i }.padding(10.dp),
                )
            }
        }
        Column(
            Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()).padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (cat) {
                0 -> {
                    Text("Color de acento", color = Breeze.Text)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Accents.forEach { c ->
                            Box(
                                Modifier.size(36.dp).clip(CircleShape).background(Color(c))
                                    .border(if (c == curAccent) 3.dp else 0.dp, Color.White, CircleShape)
                                    .clickable { onAccent(c) }
                            )
                        }
                    }
                    Text("Fondo de escritorio", color = Breeze.Text)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Wallpapers.forEachIndexed { i, w ->
                            Box(
                                Modifier.size(72.dp, 44.dp).clip(RoundedCornerShape(4.dp))
                                    .background(Brush.linearGradient(w))
                                    .border(if (i == curWall) 3.dp else 1.dp, if (i == curWall) Color.White else Breeze.Border, RoundedCornerShape(4.dp))
                                    .clickable { onWall(i) }
                            )
                        }
                    }
                    Text("Tema: Breeze Dark", color = Breeze.Muted, fontSize = 12.sp)
                }
                1 -> {
                    val dm = ctx.getSystemService(DisplayManager::class.java)
                    val displays = remember(tick) { dm.displays.toList() }
                    Button({ tick++ }) { Text("Actualizar pantallas") }
                    displays.forEach { d ->
                        val m = d.mode
                        Text("${d.name}\n${m.physicalWidth}×${m.physicalHeight} · id ${d.displayId}", color = Breeze.Text, fontSize = 12.sp)
                        if (d.displayId != Display.DEFAULT_DISPLAY) {
                            Button({
                                try {
                                    val o = ActivityOptions.makeBasic().setLaunchDisplayId(d.displayId)
                                    ctx.startActivity(
                                        Intent(ctx, DesktopActivity::class.java)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK),
                                        o.toBundle(),
                                    )
                                } catch (_: Exception) {}
                            }) { Text("Abrir escritorio aquí") }
                        }
                    }
                }
                else -> {
                    Text("Modelo: ${Build.MANUFACTURER} ${Build.MODEL}", color = Breeze.Text, fontSize = 13.sp)
                    Text("Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})", color = Breeze.Text, fontSize = 13.sp)
                    Text("PocketOS Shell 0.1.0", color = Breeze.Muted, fontSize = 12.sp)
                    Button({ openSystem(ctx, Settings.ACTION_HOME_SETTINGS) }) { Text("App de inicio predeterminada") }
                    Button({ openSystem(ctx, Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS) }) { Text("Opciones de desarrollador") }
                    Button({ openSystem(ctx, Settings.ACTION_DISPLAY_SETTINGS) }) { Text("Pantalla de Android") }
                }
            }
        }
    }
}
