package com.pocketos.desktop

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.pocketos.core.window.Win
import com.pocketos.core.window.WindowManager
import com.pocketos.design.Breeze
import com.pocketos.design.LocalAccent

@Composable
fun WindowFrame(w: Win, wm: WindowManager, content: @Composable () -> Unit) {
    val density = LocalDensity.current.density
    val accent = LocalAccent.current
    val active = wm.focusedId == w.id
    val shape = RoundedCornerShape(if (w.maximized) 0.dp else 6.dp)
    val base = if (w.maximized) Modifier.fillMaxSize()
    else Modifier.offset(w.x.dp, w.y.dp).size(w.w.dp, w.h.dp)

    Box(
        base.zIndex(w.z.toFloat())
            .clip(shape)
            .background(Breeze.Bg)
            .border(1.dp, if (active) accent else Breeze.Border, shape)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    wm.focus(w)
                }
            }
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().height(34.dp).background(Breeze.Panel)
                    .pointerInput(w.maximized) {
                        if (!w.maximized) {
                            detectDragGestures { change, drag ->
                                change.consume()
                                w.x += drag.x / density
                                w.y += drag.y / density
                            }
                        }
                    }
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(w.title, color = Breeze.Text, fontSize = 13.sp, maxLines = 1, modifier = Modifier.weight(1f))
                CtrlBtn("–") { w.minimized = true }
                CtrlBtn(if (w.maximized) "❐" else "□") { w.maximized = !w.maximized }
                CtrlBtn("✕") { wm.close(w) }
            }
            Box(Modifier.weight(1f).fillMaxWidth()) { content() }
        }
        if (!w.maximized) {
            Box(
                Modifier.align(Alignment.BottomEnd).size(24.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, drag ->
                            change.consume()
                            w.w = (w.w + drag.x / density).coerceAtLeast(280f)
                            w.h = (w.h + drag.y / density).coerceAtLeast(200f)
                        }
                    },
                contentAlignment = Alignment.BottomEnd,
            ) { Text("◢", color = Breeze.Muted, fontSize = 14.sp) }
        }
    }
}

@Composable
private fun CtrlBtn(label: String, onClick: () -> Unit) {
    Box(
        Modifier.size(width = 38.dp, height = 34.dp).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(label, color = Color.White, fontSize = 14.sp) }
}
