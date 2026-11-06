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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Taskbar(
    wins: List<Win>, desk: Int, focusedId: Int,
    onMenu: () -> Unit, onWin: (Win) -> Unit, onDesk: (Int) -> Unit,
    onFiles: () -> Unit = {}, onSettings: () -> Unit = {}, onBrowser: () -> Unit = {},
    onDiscover: () -> Unit = {}, onTerminal: () -> Unit = {}, apps: List<AppInfo> = emptyList(),
    onShowDesktop: () -> Unit = {},
) {
    val accent = LocalAccent.current
    val now = rememberNow()
    Row(
        Modifier.fillMaxWidth().height(52.dp).background(Color(0xB81B1E20)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.padding(start = 6.dp, end = 4.dp).size(44.dp).clip(RoundedCornerShape(4.dp)).clickable(onClick = onMenu),
            contentAlignment = Alignment.Center,
        ) { BreezeIcon("launcher-kde", Modifier.size(36.dp), "Application Launcher") }
        listOf("settings", "discover", "dolphin", "firefox", "terminal").forEachIndexed { i, icon ->
            Box(Modifier.padding(horizontal = 2.dp).size(38.dp, 44.dp).clip(RoundedCornerShape(4.dp))
                .clickable { when (i) { 0 -> onSettings(); 1 -> onDiscover(); 2 -> onFiles(); 3 -> onBrowser(); else -> onTerminal() } }, contentAlignment = Alignment.Center) {
                val installed = when (i) {
                    3 -> apps.firstOrNull { it.label.contains("firefox", true) || it.label.contains("browser", true) || it.label.contains("chrome", true) }
                    else -> null
                }
                if (installed?.icon != null) Image(installed.icon, contentDescription = installed.label, modifier = Modifier.size(30.dp))
                else if (icon == "firefox") PanelGlyph(4, Modifier.size(30.dp))
                else BreezeIcon(icon, Modifier.size(32.dp), icon)
            }
        }
        Row(Modifier.weight(1f).horizontalScroll(rememberScrollState()).widthIn(min = 1.dp)) {
            wins.filter { it.desk == desk }.forEach { w ->
                val active = w.id == focusedId && !w.minimized
                val tShape = RoundedCornerShape(4.dp)
                Box(
                    Modifier.padding(horizontal = 2.dp).height(44.dp).widthIn(min = 120.dp, max = 180.dp)
                        .clip(tShape)
                        .background(if (active) accent.copy(alpha = .22f) else Color(0x14FCFCFC))
                        .then(if (active) Modifier.border(1.dp, accent, tShape) else Modifier)
                        .clickable { onWin(w) }.padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (w.icon != null) Image(w.icon, null, Modifier.size(22.dp))
                        else BreezeIcon(
                            when (w.kind) { "files" -> "dolphin"; "settings" -> "settings"; else -> "terminal" },
                            Modifier.size(22.dp), null,
                        )
                        Spacer(Modifier.width(7.dp))
                        Text(w.title, color = Breeze.Text, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth(if (active) 1f else .35f).height(2.dp)
                        .background(if (active) accent else Color(0x80FCFCFC)))
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.padding(start = 8.dp, end = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf("notifications", "clipboard", "volume-muted", "brightness", "display", "arrow-up").forEach { icon ->
                BreezeIcon(icon, Modifier.padding(horizontal = 3.dp).size(18.dp), icon)
            }
            Column(Modifier.padding(start = 8.dp), horizontalAlignment = Alignment.End) {
                Text(now.format(DateTimeFormatter.ofPattern("h:mm a", java.util.Locale.US)), color = Breeze.Text, fontSize = 14.sp, lineHeight = 15.sp)
                Text(now.format(DateTimeFormatter.ofPattern("dd/MM/yy")), color = Breeze.Text, fontSize = 11.sp, lineHeight = 12.sp)
            }
            Box(Modifier.padding(start = 10.dp).size(width = 18.dp, height = 28.dp)
                .combinedClickable(onClick = onShowDesktop, onLongClick = { onDesk((desk + 1) % 4) }), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(13.dp)) { drawRect(Color(0xFFFCFCFC), style = Stroke(1.2.dp.toPx())) }
            }
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

private const val UserName = "Senthilkumar Palani"
private val KSidebar = Color(0xB3292532)
private val KContent = Color(0xC71B1E20)
private val KHeader = Color(0xCC232629)
private val KLine = Color(0x33FCFCFC)

@Composable
private fun KItemBg(selected: Boolean, hovered: Boolean, accent: Color, shape: RoundedCornerShape): Modifier =
    if (selected || hovered)
        Modifier.background(accent.copy(alpha = if (selected) .28f else .22f), shape).border(1.dp, accent.copy(alpha = .85f), shape)
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
    val categories = listOf("Favorites", "All Applications", "Development", "Graphics", "Help", "Internet", "Multimedia", "Office", "System", "Utilities")
    val catIcons = listOf("kick-bookmark", "category-all", "category-development", "category-graphics", "help", "category-internet", "category-multimedia", "category-office", "category-system", "category-utilities")

    fun find(vararg keys: String) = apps.firstOrNull { a -> keys.any { a.label.contains(it, true) } }
    // Same six favorites, same order, as the Plasma 6.4 Kickoff reference.
    val reference = listOf(
        find("firefox", "browser", "chrome") ?: AppInfo("Firefox", "pocketos:none:firefox"),
        Builtins[1],
        Builtins[0],
        find("kwrite", "text editor", "notes") ?: AppInfo("KWrite", "pocketos:none:kwrite", breeze = "kwrite"),
        find("termux", "terminal") ?: AppInfo("Konsole", "pocketos:none:konsole", breeze = "terminal"),
        find("play store", "discover", "app store") ?: AppInfo("Discover", "pocketos:none:discover", breeze = "discover"),
    )
    val searching = q.isNotBlank()
    val selected = when {
        searching -> all.filter { it.label.contains(q, true) }
        category == 0 -> (reference + favs.mapNotNull { p -> all.find { it.pkg == p } }).distinctBy { it.pkg }
        category == 1 -> all
        else -> all.filter { app ->
            when (category) {
                2 -> app.label.contains("dev", true) || app.label.contains("code", true) || app.label.contains("terminal", true) || app.label.contains("termux", true)
                3 -> app.label.contains("photo", true) || app.label.contains("camera", true) || app.label.contains("image", true) || app.label.contains("gallery", true)
                4 -> app.label.contains("help", true)
                5 -> app.label.contains("browser", true) || app.label.contains("chrome", true) || app.label.contains("firefox", true)
                6 -> app.label.contains("music", true) || app.label.contains("video", true) || app.label.contains("player", true)
                7 -> app.label.contains("office", true) || app.label.contains("docs", true) || app.label.contains("sheets", true)
                8 -> app.label.contains("settings", true) || app.label.contains("ajustes", true) || app.label.contains("clock", true)
                else -> app.label.contains("file", true) || app.label.contains("archiv", true) || app.label.contains("calc", true)
            }
        }
    }
    val shape = RoundedCornerShape(6.dp)
    Column(
        modifier.widthIn(max = 655.dp).fillMaxWidth().height(516.dp)
            .shadow(18.dp, shape, clip = false, ambientColor = Color.Black, spotColor = Color.Black).clip(shape)
            .background(KContent).border(1.dp, Color(0xFF45484C), shape)
            .pointerInput(Unit) { detectTapGestures { } },
    ) {
        // Header: avatar + user name, search field, configure + pin buttons
        Row(Modifier.fillMaxWidth().height(46.dp).background(KHeader), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.width(239.dp).padding(start = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(28.dp).clip(CircleShape).border(1.dp, Color(0xFF8BC34A), CircleShape), contentAlignment = Alignment.Center) {
                    Text("SP", color = Color(0xFF8BC34A), fontSize = 9.sp)
                }
                Spacer(Modifier.width(5.dp))
                Text(UserName, color = Breeze.Text, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            val fieldShape = RoundedCornerShape(4.dp)
            Row(
                Modifier.weight(1f).widthIn(max = 340.dp).height(30.dp).clip(fieldShape)
                    .background(Color(0x991B1E20)).border(1.dp, Color(0x663A3F44), fieldShape).padding(horizontal = 8.dp),
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
            Spacer(Modifier.weight(.001f))
            BreezeIcon("kick-configure", Modifier.padding(start = 12.dp).size(22.dp), "Configure")
            BreezeIcon("kick-pin", Modifier.padding(start = 12.dp, end = 12.dp).size(22.dp), "Keep open")
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(KLine))
        Row(Modifier.weight(1f).fillMaxWidth()) {
            // Sidebar
            Column(Modifier.width(235.dp).fillMaxHeight().background(KSidebar).padding(horizontal = 4.dp, vertical = 6.dp)) {
                categories.forEachIndexed { i, t ->
                    if (i == 2) Box(Modifier.padding(vertical = 5.dp, horizontal = 4.dp).fillMaxWidth().height(1.dp).background(KLine))
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
                        if (i == 8) Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF27AE60)))
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
        // Footer: Applications / Places tabs and power actions
        Row(Modifier.fillMaxWidth().height(46.dp).background(KHeader), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(start = 7.dp).width(114.dp).fillMaxHeight().clickable { category = 1; q = "" }) {
                Box(Modifier.align(Alignment.TopStart).padding(horizontal = 6.dp).fillMaxWidth().height(3.dp).background(accent))
                Row(Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
                    BreezeIcon("kick-apps", Modifier.size(18.dp), "Applications")
                    Spacer(Modifier.width(8.dp))
                    Text("Applications", color = Breeze.Text, fontSize = 13.sp, maxLines = 1, softWrap = false)
                }
            }
            Row(Modifier.padding(start = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                BreezeIcon("kick-places", Modifier.size(18.dp), "Places")
                Spacer(Modifier.width(8.dp))
                Text("Places", color = Breeze.Text, fontSize = 13.sp, maxLines = 1, softWrap = false)
            }
            Spacer(Modifier.weight(1f))
            listOf("kick-sleep" to "Sleep", "kick-restart" to "Restart", "kick-shutdown" to "Shut Down", "kick-session" to "Session").forEach { (ic, label) ->
                Row(Modifier.clip(RoundedCornerShape(4.dp)).clickable { }.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    BreezeIcon(ic, Modifier.size(16.dp), label)
                    Spacer(Modifier.width(5.dp))
                    Text(label, color = Breeze.Text, fontSize = 13.sp, maxLines = 1)
                }
            }
            Spacer(Modifier.width(4.dp))
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
