package com.pocketos.shell

import androidx.compose.foundation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketos.core.applications.AppInfo
import com.pocketos.core.applications.Builtins
import com.pocketos.core.window.Win
import com.pocketos.design.Breeze
import com.pocketos.design.BreezeIcon
import com.pocketos.design.Dock
import com.pocketos.design.LocalAccent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

@Composable
fun rememberNow(): LocalDateTime {
    val s by produceState(LocalDateTime.now()) {
        while (true) { delay(5000); value = LocalDateTime.now() }
    }
    return s
}

@Composable
private fun PanelGlyph(kind: Int, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        val c = Color(0xFFE6E8EA)
        when (kind) {
            0 -> { // KDE gear-like launcher mark
                drawCircle(Color(0xFF3DAEE9), radius = w * .38f)
                drawCircle(Color(0xFF1D2328), radius = w * .20f)
                for (i in 0..7) {
                    val a = Math.PI * i / 4
                    val x = (w / 2 + kotlin.math.cos(a).toFloat() * w * .43f)
                    val y = (h / 2 + kotlin.math.sin(a).toFloat() * h * .43f)
                    drawCircle(Color(0xFF3DAEE9), radius = w * .1f, center = androidx.compose.ui.geometry.Offset(x,y))
                }
            }
            1 -> { // system settings sliders
                for (y in listOf(.25f, .5f, .75f)) drawLine(c, androidx.compose.ui.geometry.Offset(w*.12f,h*y), androidx.compose.ui.geometry.Offset(w*.88f,h*y), 1.5.dp.toPx())
                drawCircle(Color(0xFF3DAEE9), w*.13f, androidx.compose.ui.geometry.Offset(w*.37f,h*.25f))
                drawCircle(Color(0xFF3DAEE9), w*.13f, androidx.compose.ui.geometry.Offset(w*.67f,h*.5f))
                drawCircle(Color(0xFF3DAEE9), w*.13f, androidx.compose.ui.geometry.Offset(w*.43f,h*.75f))
            }
            2 -> { // discover bag
                drawRoundRect(Color(0xFF3DAEE9), androidx.compose.ui.geometry.Offset(w*.12f,h*.32f), androidx.compose.ui.geometry.Size(w*.76f,h*.57f), androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()))
                drawArc(c, 180f, 180f, false, androidx.compose.ui.geometry.Offset(w*.31f,h*.12f), androidx.compose.ui.geometry.Size(w*.38f,h*.42f), style=Stroke(1.6.dp.toPx()))
            }
            3 -> { // folder
                val p = Path().apply { moveTo(w*.08f,h*.25f); lineTo(w*.43f,h*.25f); lineTo(w*.53f,h*.38f); lineTo(w*.94f,h*.38f); lineTo(w*.88f,h*.82f); lineTo(w*.12f,h*.82f); close() }
                drawPath(p, Color(0xFF39A9E8))
                drawRoundRect(Color(0xFF78C8F0), androidx.compose.ui.geometry.Offset(w*.12f,h*.3f), androidx.compose.ui.geometry.Size(w*.75f,h*.48f), androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()))
            }
            4 -> { // firefox-like warm globe
                drawCircle(Color(0xFFFF8A24), w*.43f)
                drawArc(Color(0xFFE84C89), 210f, 280f, false, androidx.compose.ui.geometry.Offset(w*.15f,h*.12f), androidx.compose.ui.geometry.Size(w*.75f,h*.75f), style=Stroke(2.dp.toPx()))
                drawCircle(Color(0xFF5A4BBA), w*.22f, androidx.compose.ui.geometry.Offset(w*.55f,h*.55f))
            }
            5 -> { // terminal
                drawRoundRect(Color(0xFF454B4D), androidx.compose.ui.geometry.Offset(w*.08f,h*.1f), androidx.compose.ui.geometry.Size(w*.84f,h*.8f), androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()))
                val p=Path().apply { moveTo(w*.35f,h*.32f); lineTo(w*.53f,h*.5f); lineTo(w*.35f,h*.68f) }; drawPath(p,c,style=Stroke(2.dp.toPx()))
                drawLine(c, androidx.compose.ui.geometry.Offset(w*.56f,h*.68f), androidx.compose.ui.geometry.Offset(w*.75f,h*.68f),2.dp.toPx())
            }
            6 -> { // bell / notification indicator
                drawArc(c, 180f, 180f, false, androidx.compose.ui.geometry.Offset(w*.18f,h*.2f), androidx.compose.ui.geometry.Size(w*.64f,h*.66f), style=Stroke(1.5.dp.toPx()))
                drawLine(c,androidx.compose.ui.geometry.Offset(w*.18f,h*.68f),androidx.compose.ui.geometry.Offset(w*.82f,h*.68f),1.5.dp.toPx())
                drawCircle(c,w*.07f,androidx.compose.ui.geometry.Offset(w*.5f,h*.84f))
            }
            7 -> { // clipboard
                drawRoundRect(c,androidx.compose.ui.geometry.Offset(w*.2f,h*.2f),androidx.compose.ui.geometry.Size(w*.6f,h*.7f),androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),style=Stroke(1.4.dp.toPx()))
                drawRoundRect(color = Color(0xFF1B1E20),topLeft = androidx.compose.ui.geometry.Offset(w*.35f,h*.12f),size = androidx.compose.ui.geometry.Size(w*.3f,h*.2f),cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()))
                for(y in listOf(.48f,.63f,.77f)) drawLine(c,androidx.compose.ui.geometry.Offset(w*.32f,h*y),androidx.compose.ui.geometry.Offset(w*.68f,h*y),1.dp.toPx())
            }
            8 -> { // muted speaker
                val p=Path().apply { moveTo(w*.12f,h*.4f); lineTo(w*.32f,h*.4f); lineTo(w*.56f,h*.2f); lineTo(w*.56f,h*.8f); lineTo(w*.32f,h*.6f); lineTo(w*.12f,h*.6f); close() }; drawPath(p,c)
                drawLine(c,androidx.compose.ui.geometry.Offset(w*.67f,h*.38f),androidx.compose.ui.geometry.Offset(w*.88f,h*.62f),1.5.dp.toPx()); drawLine(c,androidx.compose.ui.geometry.Offset(w*.88f,h*.38f),androidx.compose.ui.geometry.Offset(w*.67f,h*.62f),1.5.dp.toPx())
            }
            9 -> { // brightness
                drawCircle(c,w*.25f,style=Stroke(1.4.dp.toPx()))
                for(i in 0..7) { val a=Math.PI*i/4; drawLine(c, androidx.compose.ui.geometry.Offset(w*.5f+kotlin.math.cos(a).toFloat()*w*.33f,h*.5f+kotlin.math.sin(a).toFloat()*h*.33f), androidx.compose.ui.geometry.Offset(w*.5f+kotlin.math.cos(a).toFloat()*w*.45f,h*.5f+kotlin.math.sin(a).toFloat()*h*.45f),1.2.dp.toPx()) }
            }
            10 -> { // display
                drawRoundRect(c,androidx.compose.ui.geometry.Offset(w*.12f,h*.16f),androidx.compose.ui.geometry.Size(w*.76f,h*.56f),androidx.compose.ui.geometry.CornerRadius(1.dp.toPx()),style=Stroke(1.4.dp.toPx()))
                drawLine(c,androidx.compose.ui.geometry.Offset(w*.5f,h*.72f),androidx.compose.ui.geometry.Offset(w*.5f,h*.87f),1.4.dp.toPx()); drawLine(c,androidx.compose.ui.geometry.Offset(w*.34f,h*.88f),androidx.compose.ui.geometry.Offset(w*.66f,h*.88f),1.4.dp.toPx())
            }
            else -> { // system tray symbols
                drawCircle(c,w*.34f,style=Stroke(1.4.dp.toPx()))
                drawLine(c,androidx.compose.ui.geometry.Offset(w*.5f,h*.08f),androidx.compose.ui.geometry.Offset(w*.5f,h*.92f),1.4.dp.toPx())
                drawLine(c,androidx.compose.ui.geometry.Offset(w*.08f,h*.5f),androidx.compose.ui.geometry.Offset(w*.92f,h*.5f),1.4.dp.toPx())
            }
        }
    }
}

@Composable
private fun DockSlot(
    running: Boolean, active: Boolean, onClick: () -> Unit,
    lit: Boolean = false, content: @Composable BoxScope.() -> Unit,
) {
    val src = remember { MutableInteractionSource() }
    val hovered by src.collectIsHoveredAsState()
    Box(
        Modifier.width(Dock.Pitch.dp).fillMaxHeight().hoverable(src)
            .clickable(interactionSource = src, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (hovered || lit) Box(
            Modifier.size((Dock.IconSize + 12f).dp).offset(y = (-Dock.IconsLift).dp)
                .background(Color(0x1FFFFFFF), RoundedCornerShape(14.dp)),
        )
        Box(
            Modifier.size(Dock.IconSize.dp).offset(y = (-Dock.IconsLift).dp),
            contentAlignment = Alignment.Center, content = content,
        )
        // Running-app indicator: long, thin, light bar under the icon (68 x 3 dp).
        if (running) Box(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 3.dp).width(68.dp).height(3.dp)
                .background(Color.White.copy(alpha = if (active) .85f else .5f), RoundedCornerShape(2.dp)),
        )
    }
}

/**
 * Floating dock replicated from the reference: workspace pill on the left, app icons,
 * clock on the right, on a translucent graphite panel.
 */
@Composable
fun Taskbar(
    wins: List<Win>, desk: Int, focusedId: Int,
    onMenu: () -> Unit, onWin: (Win) -> Unit, onDesk: (Int) -> Unit,
    onFiles: () -> Unit = {}, onSettings: () -> Unit = {}, onBrowser: () -> Unit = {},
    onTerminal: () -> Unit = {}, apps: List<AppInfo> = emptyList(),
    onShowDesktop: () -> Unit = {},
    modifier: Modifier = Modifier,
    menuOpen: Boolean = false,
    desks: Int = 3,
) {
    val accent = LocalAccent.current
    val now = rememberNow()
    val here = wins.filter { it.desk == desk }
    fun pkgOf(w: Win) = if (w.kind.contains(":")) w.kind.substringAfter(":") else ""
    val browser = apps.firstOrNull { it.label.contains("chrome", true) } ?: apps.firstOrNull { it.label.contains("browser", true) || it.label.contains("firefox", true) }
    val term = apps.firstOrNull { it.label.contains("termux", true) || it.label.contains("terminal", true) }
    fun winFor(k: DockIconKind): Win? = here.firstOrNull { w ->
        when (k) {
            DockIconKind.Settings -> w.kind == "settings"
            DockIconKind.Files -> w.kind == "files"
            DockIconKind.Chrome -> browser != null && pkgOf(w) == browser.pkg
            DockIconKind.Terminal -> term != null && pkgOf(w) == term.pkg
            else -> false
        }
    }
    val pinned = listOf(DockIconKind.Settings, DockIconKind.Terminal, DockIconKind.Files, DockIconKind.Chrome)
    val pinnedWins = pinned.mapNotNull { winFor(it)?.id }
    val extras = here.filter { it.id !in pinnedWins }.take(6)
    fun isActive(w: Win?) = w != null && w.id == focusedId && !w.minimized

    Box(
        modifier.fillMaxWidth().height(Dock.Height.dp).background(Breeze.Panel.copy(alpha = Dock.Alpha)),
    ) {
        // 1 px lighter top edge seen in the reference
        Box(Modifier.align(Alignment.TopCenter).fillMaxWidth().height(1.5.dp).background(Color(0x14FFFFFF)))

        // ---- Left: workspace indicator (dot, active pill, dot) --------------------------------
        Row(Modifier.align(Alignment.CenterStart).padding(start = 3.5.dp).fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            for (i in 0 until desks) {
                Box(
                    Modifier.fillMaxHeight().padding(horizontal = 3.5.dp)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDesk(i) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (i == desk) Box(Modifier.size(45.dp, 15.dp).background(accent, CircleShape))
                    else Box(
                        Modifier.size(12.dp).background(Color(0xFF8E8D93), CircleShape)
                            .border(1.5.dp, Color(0xFFC2C1C6), CircleShape),
                    )
                }
            }
        }

        // ---- Centre: launcher + pinned apps + other running windows ---------------------------
        Row(Modifier.align(Alignment.CenterStart).offset(x = Dock.IconsStart.dp).fillMaxHeight()) {
            DockSlot(running = false, active = false, onClick = onMenu, lit = menuOpen) {
                DockIcon(DockIconKind.Launcher, Modifier.fillMaxSize())
            }
            pinned.forEach { kind ->
                val w = winFor(kind)
                DockSlot(
                    running = w != null, active = isActive(w),
                    onClick = {
                        if (w != null) onWin(w) else when (kind) {
                            DockIconKind.Settings -> onSettings()
                            DockIconKind.Terminal -> onTerminal()
                            DockIconKind.Files -> onFiles()
                            DockIconKind.Chrome -> onBrowser()
                            else -> onMenu()
                        }
                    },
                ) {
                    val real = if (kind == DockIconKind.Chrome) browser?.icon else null
                    if (real != null) Image(real, browser?.label, Modifier.fillMaxSize())
                    else DockIcon(kind, Modifier.fillMaxSize())
                }
            }
            extras.forEach { w ->
                DockSlot(running = true, active = isActive(w), onClick = { onWin(w) }) {
                    if (w.icon != null) Image(w.icon, w.title, Modifier.fillMaxSize())
                    else BreezeIcon(
                        when (w.kind) { "files" -> "dolphin"; "settings" -> "settings"; else -> "terminal" },
                        Modifier.fillMaxSize(), w.title,
                    )
                }
            }
        }

        // ---- Right: clock ----
        Row(
            Modifier.align(Alignment.CenterEnd).fillMaxHeight().offset(y = (-2).dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    now.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = Breeze.Text, fontSize = 24.sp, lineHeight = 24.sp, maxLines = 1, softWrap = false,
                )
                Text(
                    now.format(DateTimeFormatter.ofPattern("EEE dd", java.util.Locale.getDefault())).replace(".", ""),
                    color = Breeze.Text, fontSize = 18.sp, lineHeight = 20.sp, maxLines = 1, softWrap = false,
                )
            }
            // Invisible "show desktop" strip at the far right edge (13 dp, same as the reference margin)
            Box(
                Modifier.width(13.dp).fillMaxHeight()
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onShowDesktop),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppTile(app: AppInfo, fav: Boolean, onOpen: () -> Unit, onFav: () -> Unit) {
    Column(
        Modifier.width(84.dp).clip(RoundedCornerShape(6.dp))
            .combinedClickable(onClick = onOpen, onLongClick = onFav).padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
            if (app.icon != null) Image(app.icon, null, Modifier.fillMaxSize())
            else when (app.pkg) {
                "pocketos:files" -> BreezeIcon("dolphin", Modifier.size(42.dp), app.label)
                "pocketos:settings" -> BreezeIcon("settings", Modifier.size(42.dp), app.label)
                else -> Text(app.glyph, fontSize = 30.sp)
            }
        }
        Text(
            (if (fav) "★ " else "") + app.label, color = Breeze.Text, fontSize = 11.sp,
            maxLines = 2, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis,
        )
    }
}

private val KSidebar = Color(0xB32A2A32)
private val KContent = Color(0xD11E1E24)
private val KHeader = Color(0xD92C2C34)
private val KLine = Color(0x24FFFFFF)

@Composable
private fun KItemBg(selected: Boolean, hovered: Boolean, accent: Color, shape: RoundedCornerShape): Modifier =
    if (selected) Modifier.background(accent.copy(alpha = .85f), shape)
    else if (hovered) Modifier.background(Color(0x1FFFFFFF), shape)
    else Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KickoffTile(app: AppInfo, accent: Color, onOpen: () -> Unit, onFav: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val hovered by src.collectIsHoveredAsState()
    val shape = RoundedCornerShape(4.dp)
    Column(
        Modifier.size(103.dp, 101.dp).then(KItemBg(false, hovered, accent, shape)).clip(shape)
            .hoverable(src).combinedClickable(onClick = onOpen, onLongClick = onFav).padding(top = 8.dp, start = 4.dp, end = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { KAppIcon(app, 48) }
        Spacer(Modifier.height(6.dp))
        Text(app.label, color = Breeze.Text, fontSize = 13.sp, maxLines = 2, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp)
    }
}

@Composable
private fun KAppIcon(app: AppInfo, size: Int) {
    if (app.icon != null) Image(app.icon, null, Modifier.size(size.dp))
    else when {
        app.pkg == "pocketos:files" -> BreezeIcon("dolphin", Modifier.size(size.dp), app.label)
        app.pkg == "pocketos:settings" -> BreezeIcon("settings", Modifier.size(size.dp), app.label)
        app.breeze.isNotEmpty() -> BreezeIcon(app.breeze, Modifier.size(size.dp), app.label)
        app.pkg == "pocketos:none:firefox" -> PanelGlyph(4, Modifier.size(size.dp))
        else -> Text(app.glyph, fontSize = (size * .7f).sp)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KickoffRow(app: AppInfo, accent: Color, onOpen: () -> Unit, onFav: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val hovered by src.collectIsHoveredAsState()
    val shape = RoundedCornerShape(4.dp)
    Row(
        Modifier.fillMaxWidth().height(38.dp).then(KItemBg(false, hovered, accent, shape)).clip(shape)
            .hoverable(src).combinedClickable(onClick = onOpen, onLongClick = onFav).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) { KAppIcon(app, 28) }
        Spacer(Modifier.width(10.dp))
        Text(app.label, color = Breeze.Text, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun Kickoff(
    apps: List<AppInfo>, favs: List<String>, recents: List<String>,
    onOpen: (AppInfo) -> Unit, onFav: (AppInfo) -> Unit, modifier: Modifier = Modifier,
) {
    val accent = LocalAccent.current
    var q by remember { mutableStateOf("") }
    val all = Builtins + apps
    var category by remember { mutableIntStateOf(0) }
    val categories = listOf("Favorites", "All Applications")
    val catIcons = listOf("kick-bookmark", "category-all")

    fun find(vararg keys: String) = apps.firstOrNull { a -> keys.any { a.label.contains(it, true) } }
    // Same six favorites, same order, as the Plasma 6.4 Kickoff reference.
    val reference = listOfNotNull(
        find("chrome", "firefox", "browser"),
        Builtins[1],
        Builtins[0],
        find("kwrite", "text editor", "notes"),
        find("termux", "terminal"),
    )
    val searching = q.isNotBlank()
    val selected = when {
        searching -> all.filter { it.label.contains(q, true) }
        category == 0 -> (reference + favs.mapNotNull { p -> all.find { it.pkg == p } }).distinctBy { it.pkg }
        else -> all
    }
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier.widthIn(max = 655.dp).fillMaxWidth().height(516.dp)
            .shadow(18.dp, shape, clip = false, ambientColor = Color.Black, spotColor = Color.Black).clip(shape)
            .background(KContent).border(1.dp, Color(0x33FFFFFF), shape)
            .pointerInput(Unit) { detectTapGestures { } },
    ) {
        // Header: search field
        Row(Modifier.fillMaxWidth().height(46.dp).background(KHeader), verticalAlignment = Alignment.CenterVertically) {
            val fieldShape = RoundedCornerShape(8.dp)
            Row(
                Modifier.padding(horizontal = 12.dp).weight(1f).height(30.dp).clip(fieldShape)
                    .background(Color(0x66000000)).border(1.dp, Color(0x24FFFFFF), fieldShape).padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BreezeIcon("kick-search", Modifier.size(16.dp), "Search")
                Spacer(Modifier.width(6.dp))
                BasicTextField(
                    value = q, onValueChange = { q = it }, singleLine = true,
                    textStyle = TextStyle(color = Breeze.Text, fontSize = 13.sp),
                    cursorBrush = SolidColor(accent),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (q.isEmpty()) Text("Search…", color = Breeze.Muted, fontSize = 13.sp)
                            inner()
                        }
                    },
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(KLine))
        Row(Modifier.weight(1f).fillMaxWidth()) {
            // Sidebar
            Column(Modifier.width(235.dp).fillMaxHeight().background(KSidebar).padding(horizontal = 4.dp, vertical = 6.dp)) {
                categories.forEachIndexed { i, t ->
                    val src = remember { MutableInteractionSource() }
                    val hovered by src.collectIsHoveredAsState()
                    val sh = RoundedCornerShape(4.dp)
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 1.dp).height(33.dp)
                            .then(KItemBg(i == category && !searching, hovered, accent, sh)).clip(sh)
                            .hoverable(src).clickable { category = i; q = "" }.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BreezeIcon(catIcons[i], Modifier.size(22.dp), t)
                        Spacer(Modifier.width(12.dp))
                        Text(t, color = Breeze.Text, fontSize = 13.sp, maxLines = 1, modifier = Modifier.weight(1f))
                    }
                }
            }
            Box(Modifier.width(1.dp).fillMaxHeight().background(KLine))
            // Content
            Box(Modifier.weight(1f).fillMaxHeight()) {
                if (category == 0 && !searching) {
                    LazyVerticalGrid(
                        GridCells.Fixed(4), Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 4.dp, top = 6.dp, end = 4.dp),
                    ) {
                        items(selected) { a -> KickoffTile(a, accent, { onOpen(a) }, { onFav(a) }) }
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)) {
                        items(selected) { a -> KickoffRow(a, accent, { onOpen(a) }, { onFav(a) }) }
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(KLine))
        // Footer: Applications tab
        Row(Modifier.fillMaxWidth().height(46.dp).background(KHeader), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(start = 7.dp).width(114.dp).fillMaxHeight().clickable { category = 1; q = "" }) {
                Box(Modifier.align(Alignment.TopStart).padding(horizontal = 6.dp).fillMaxWidth().height(3.dp).background(accent))
                Row(Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
                    BreezeIcon("kick-apps", Modifier.size(18.dp), "Applications")
                    Spacer(Modifier.width(8.dp))
                    Text("Applications", color = Breeze.Text, fontSize = 13.sp, maxLines = 1, softWrap = false)
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    val now = rememberNow()
    Column(
        modifier.clip(RoundedCornerShape(10.dp)).background(Color(0x99232629)).padding(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Text(now.format(DateTimeFormatter.ofPattern("HH:mm")), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Light)
        Text(now.format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy")), color = Breeze.Muted, fontSize = 12.sp)
    }
}

@Composable
fun DesktopIcon(glyph: String, label: String, onClick: () -> Unit) {
    Column(
        Modifier.width(88.dp).clip(RoundedCornerShape(6.dp)).clickable(onClick = onClick).padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            when (glyph) {
                "📁" -> BreezeIcon("folder", Modifier.size(44.dp), label)
                "⚙" -> BreezeIcon("settings", Modifier.size(38.dp), label)
                else -> Text(glyph, fontSize = 36.sp)
            }
        }
        Text(label, color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}
