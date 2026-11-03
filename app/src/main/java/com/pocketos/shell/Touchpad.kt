package com.pocketos.shell

import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketos.core.display.Diag
import com.pocketos.core.input.Pointer
import com.pocketos.core.input.Remote
import com.pocketos.core.input.touchpad
import com.pocketos.system.PocketAccessibilityService

@Composable
private fun RoundBtn(size: Dp, glyph: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        Modifier.size(size).clip(CircleShape)
            .background(if (enabled) Color(0xFF273447) else Color(0xFF1A2434))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(glyph, color = if (enabled) Color.White else Color(0xFF5A6678), fontSize = 22.sp) }
}

@Composable
fun TouchpadScreen(
    extName: String?, a11yOn: Boolean,
    onDesktopHere: () -> Unit, onRelaunch: () -> Unit, onHome: () -> Unit,
    onOpenA11y: () -> Unit, onClose: () -> Unit,
) {
    val ctx = LocalContext.current
    val am = remember { ctx.getSystemService(AudioManager::class.java) }
    var menu by remember { mutableStateOf(false) }
    var diag by remember { mutableStateOf(false) }
    var kb by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val sensList = listOf(1.5f, 2.5f, 4f)
    val line = Color(0x88FFFFFF)

    Column(Modifier.fillMaxSize().background(Color(0xFF00142B)).systemBarsPadding().padding(horizontal = 12.dp)) {
        Row(Modifier.fillMaxWidth().height(52.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("✕", color = Color.White, fontSize = 22.sp, modifier = Modifier.clickable(onClick = onClose).padding(12.dp))
            Spacer(Modifier.weight(1f))
            Box {
                Text("⋮", color = Color.White, fontSize = 24.sp, modifier = Modifier.clickable { menu = true }.padding(12.dp))
                DropdownMenu(menu, { menu = false }) {
                    DropdownMenuItem(text = { Text("Escritorio en este teléfono") }, onClick = { menu = false; onDesktopHere() })
                    DropdownMenuItem(text = { Text("Reiniciar escritorio externo") }, onClick = { menu = false; onRelaunch() })
                    DropdownMenuItem(
                        text = { Text(if (a11yOn) "Accesibilidad: activa ✓" else "Activar accesibilidad (apps de terceros)") },
                        onClick = { menu = false; onOpenA11y() },
                    )
                    DropdownMenuItem(text = { Text("Diagnóstico") }, onClick = { menu = false; diag = true })
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("PocketOS", color = Color.White, fontSize = 22.sp, maxLines = 1)
                Text(
                    if (Pointer.active) "Conectado: ${extName ?: "pantalla"}" else extName ?: "Sin pantalla externa",
                    color = Color(0xFFBDC3C7), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RoundBtn(44.dp, "🔉") { am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI) }
                RoundBtn(44.dp, "🔊") { am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI) }
                RoundBtn(44.dp, "⌨") { kb = true }
                RoundBtn(44.dp, "🖱") {
                    val i = sensList.indexOf(Pointer.sens)
                    Pointer.sens = sensList[(i + 1) % sensList.size]
                }
                RoundBtn(44.dp, "⏻", enabled = extName != null, onClick = onRelaunch)
            }
        }
        Box(
            Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp)
                .clip(RoundedCornerShape(28.dp)).background(Color(0xFF1B2A41))
                .drawBehind {
                    drawLine(line, Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), 2f)
                    drawLine(line, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 2f)
                }
                .touchpad(),
        ) {
            Box(
                Modifier.padding(12.dp).size(60.dp).clip(CircleShape).background(Color(0xFFFF9F43)),
                contentAlignment = Alignment.Center,
            ) { Text("🖱", fontSize = 28.sp) }
            if (!Pointer.active) {
                Text(
                    "Conecta un monitor o TV por USB-C.\nEl escritorio se abrirá allí.",
                    color = Color(0xFFBDC3C7), textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                )
            }
            Text(
                "Sensibilidad ×${Pointer.sens} · toque = clic · mantener = arrastrar · 2 dedos = scroll",
                color = Color(0xFF7F8C9B), fontSize = 10.sp, textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
            )
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
        ) {
            RoundBtn(76.dp, "←") { Remote.send("back") }
            RoundBtn(96.dp, "⌂", onClick = onHome)
            RoundBtn(96.dp, "▢") { Remote.send("switch") }
            RoundBtn(76.dp, "≡") { Remote.send("menu") }
        }
    }

    if (diag) DiagDialog { diag = false }
    if (kb) {
        AlertDialog(
            onDismissRequest = { kb = false },
            title = { Text("Escribir en la TV") },
            text = {
                Column {
                    TextField(text, { text = it })
                    if (!a11yOn) Text("Requiere activar la accesibilidad (menú ⋮).", fontSize = 11.sp)
                }
            },
            confirmButton = {
                TextButton({
                    val ok = PocketAccessibilityService.instance?.typeText(text, false) ?: false
                    if (!ok) Diag.d("Teclado: no hay campo de texto con foco en la TV")
                    else text = ""
                }) { Text("Enviar") }
            },
            dismissButton = {
                Row {
                    TextButton({ PocketAccessibilityService.instance?.typeText("", true) }) { Text("⌫") }
                    TextButton({ kb = false }) { Text("Cerrar") }
                }
            },
        )
    }
}
