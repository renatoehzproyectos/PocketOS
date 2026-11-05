package com.pocketos.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import com.pocketos.core.window.Win
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import com.pocketos.core.applications.*
import com.pocketos.core.storage.*
import com.pocketos.core.input.Remote
import com.pocketos.core.window.WindowManager
import com.pocketos.design.Wallpapers
import com.pocketos.design.drawPlasmaWallpaper
import com.pocketos.applications.FileManager
import com.pocketos.applications.SettingsApp
import com.pocketos.shell.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Desktop(prefs: Preferences) {
    val ctx = LocalContext.current
    val dens = LocalDensity.current.density
    fun boundsOf(w: Win) = android.graphics.Rect(
        (w.x * dens).toInt(), (w.y * dens).toInt(), ((w.x + w.w) * dens).toInt(), ((w.y + w.h) * dens).toInt())
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

    LaunchedEffect(Unit) {
        Remote.cmds.collect { c ->
            val here = wm.wins.filter { it.desk == desk }
            when (c) {
                "menu" -> menu = !menu
                "back" -> {
                    if (menu) menu = false
                    else here.filter { !it.minimized }.maxByOrNull { it.z }?.let { wm.close(it) }
                }
                "home" -> {
                    menu = false
                    val vis = here.filter { !it.minimized }
                    if (vis.isNotEmpty()) vis.forEach { it.minimized = true }
                    else here.forEach { it.minimized = false }
                }
                "switch" -> {
                    if (here.isNotEmpty()) {
                        val i = here.indexOfFirst { it.id == wm.focusedId }
                        wm.focus(here[(i + 1) % here.size])
                    }
                }
            }
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black).systemBarsPadding()) {
        val aw = maxWidth.value
        val ah = maxHeight.value - 52f

        fun openApp(a: AppInfo) {
            menu = false
            when (a.pkg) {
                "pocketos:files" -> wm.open("files", "Dolphin", desk, aw, ah)
                "pocketos:settings" -> wm.open("settings", "Global Theme — System Settings", desk, aw, ah)
                else -> if (!a.pkg.startsWith("pocketos:")) {
                    val w = wm.open("app:${a.pkg}", a.label, desk, aw, ah, a.pkg)
                    val top = 0  // desktop area starts below system bars padding
                    launchApp(ctx, a.pkg, displayId, boundsOf(w))
                    scope.launch { ctx.pushRecent(a.pkg) }
                }
            }
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                if ((prefs[Keys.WALL] ?: 0) == 0) Canvas(Modifier.fillMaxSize()) { drawPlasmaWallpaper(this) }
                else Box(Modifier.fillMaxSize().background(Brush.linearGradient(wall)))
                Box(Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures { menu = false } })
                Column(Modifier.padding(start = 34.dp, top = 26.dp)) {
                    DesktopIcon("📁", "Files") { openApp(Builtins[0]) }
                }
                wm.wins.forEach { w ->
                    key(w.id) {
                        if (w.desk == desk && !w.minimized && w.pkg.isEmpty()) {
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
                        Modifier.align(Alignment.BottomStart).padding(start = 8.dp, bottom = 8.dp).zIndex(100000f),
                    )
                }
            }
            Taskbar(
                wm.wins, desk, wm.focusedId,
                onMenu = { menu = !menu },
                onWin = { w ->
                    menu = false
                    if (w.pkg.isNotEmpty()) {
                        // Real Android app: can't be minimized from here. Focused -> remove entry; else bring back.
                        if (wm.focusedId == w.id) wm.close(w)
                        else {
                            wm.focus(w)
                            launchApp(ctx, w.pkg, displayId, boundsOf(w))
                        }
                    } else wm.fromTaskbar(w)
                },
                onDesk = { desk = it; menu = false },
                onFiles = { openApp(Builtins[0]) },
                onSettings = { openApp(Builtins[1]) },
                onBrowser = { apps.firstOrNull { it.label.contains("browser", true) || it.label.contains("firefox", true) || it.label.contains("chrome", true) }?.let(::openApp) ?: run { menu = true } },
                onDiscover = { apps.firstOrNull { it.label.contains("play store", true) || it.label.contains("discover", true) || it.label.contains("app store", true) }?.let(::openApp) ?: run { menu = true } },
                onTerminal = { apps.firstOrNull { it.label.contains("termux", true) || it.label.contains("terminal", true) }?.let(::openApp) ?: run { menu = true } },
                apps = apps,
            )
        }
    }
}
