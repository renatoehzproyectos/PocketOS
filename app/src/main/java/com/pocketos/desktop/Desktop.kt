package com.pocketos.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import com.pocketos.core.applications.*
import com.pocketos.core.storage.*
import com.pocketos.core.window.WindowManager
import com.pocketos.design.Wallpapers
import com.pocketos.applications.FileManager
import com.pocketos.applications.SettingsApp
import com.pocketos.shell.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Desktop(prefs: Preferences) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val wm = remember { WindowManager() }
    var desk by remember { mutableIntStateOf(0) }
    var menu by remember { mutableStateOf(false) }
    val apps by produceState(emptyList<AppInfo>()) {
        value = withContext(Dispatchers.IO) { loadApps(ctx) }
    }
    val displayId = remember { ctx.display?.displayId ?: 0 }
    val wall = Wallpapers[(prefs[Keys.WALL] ?: 0).coerceIn(0, Wallpapers.lastIndex)]
    val favs = prefs.strList(Keys.FAVS)
    val recents = prefs.strList(Keys.RECENTS)

    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black).systemBarsPadding()) {
        val aw = maxWidth.value
        val ah = maxHeight.value - 46f

        fun openApp(a: AppInfo) {
            menu = false
            when (a.pkg) {
                "pocketos:files" -> wm.open("files", "📁 Archivos", desk, aw, ah)
                "pocketos:settings" -> wm.open("settings", "⚙ Ajustes del sistema", desk, aw, ah)
                else -> {
                    launchApp(ctx, a.pkg, displayId)
                    scope.launch { ctx.pushRecent(a.pkg) }
                }
            }
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f).fillMaxWidth().background(Brush.linearGradient(wall))) {
                Box(Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures { menu = false } })
                Column(Modifier.padding(12.dp)) {
                    DesktopIcon("📁", "Archivos") { openApp(Builtins[0]) }
                    Spacer(Modifier.height(10.dp))
                    DesktopIcon("⚙", "Ajustes") { openApp(Builtins[1]) }
                }
                ClockWidget(Modifier.align(Alignment.TopEnd).padding(16.dp))
                wm.wins.forEach { w ->
                    key(w.id) {
                        if (w.desk == desk && !w.minimized) {
                            WindowFrame(w, wm) {
                                when (w.kind) {
                                    "files" -> FileManager()
                                    "settings" -> SettingsApp(
                                        prefs,
                                        { c -> scope.launch { ctx.setAccent(c) } },
                                        { i -> scope.launch { ctx.setWall(i) } },
                                    )
                                }
                            }
                        }
                    }
                }
                if (menu) {
                    Kickoff(
                        apps, favs, recents, { a -> openApp(a) },
                        { a -> scope.launch { ctx.toggleFav(a.pkg) } },
                        Modifier.align(Alignment.BottomStart).padding(6.dp),
                    )
                }
            }
            Taskbar(
                wm.wins, desk, wm.focusedId,
                onMenu = { menu = !menu },
                onWin = { wm.fromTaskbar(it); menu = false },
                onDesk = { desk = it; menu = false },
            )
        }
    }
}
