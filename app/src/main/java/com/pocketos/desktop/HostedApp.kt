package com.pocketos.desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.pocketos.core.window.Win
import com.pocketos.vd.VdHost
import com.pocketos.vd.VdSession
import kotlin.math.roundToInt

/** Content of a Plasma window that shows a real Android app running on a virtual display. */
@Composable
fun HostedApp(w: Win, pkg: String, hidden: Boolean, onFocus: () -> Unit) {
    val ctx = LocalContext.current
    // The shell is laid out at 1920 px reference width, so density == scale. 160 dpi * scale
    // gives hosted apps a desktop-sized layout (a 930 dp wide window is a tablet layout).
    val dpi = (160f * LocalDensity.current.density).roundToInt().coerceIn(120, 640)
    val session = remember(w.id) { VdSession(ctx.applicationContext, pkg, w.title, dpi) }
    session.hidden = hidden
    session.onFocus = onFocus
    DisposableEffect(w.id) {
        VdHost.sessions[w.id] = session
        onDispose { VdHost.sessions.remove(w.id); session.release() }
    }
    LaunchedEffect(VdHost.service) { session.tryStart() }
    AndroidView(factory = { session.createView(it) }, modifier = Modifier.fillMaxSize())
}
