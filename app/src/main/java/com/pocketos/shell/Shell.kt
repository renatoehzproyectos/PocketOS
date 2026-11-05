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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun Taskbar(
    wins: List<Win>, desk: Int, focusedId: Int,
    onMenu: () -> Unit, onWin: (Win) -> Unit, onDesk: (Int) -> Unit,
    onFiles: () -> Unit = {}, onSettings: () -> Unit = {}, onBrowser: () -> Unit = {},
    onDiscover: () -> Unit = {}, onTerminal: () -> Unit = {}, apps: List<AppInfo> = emptyList(),
) {
    val accent = LocalAccent.current
    val now = rememberNow()
    Row(
        Modifier.fillMaxWidth().height(52.dp).background(Color(0xF21B1E20)).border(1.dp, Color(0x334D4D4D)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.padding(start = 9.dp, end = 5.dp).size(40.dp).clip(CircleShape)
                .background(Color(0xFF202428)).border(1.dp, accent.copy(alpha = .72f), RoundedCornerShape(50)).clickable(onClick = onMenu),
            contentAlignment = Alignment.Center,
        ) { BreezeIcon("launcher-kde", Modifier.size(32.dp), "KDE applications") }
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
                Box(
                    Modifier.padding(horizontal = 3.dp).height(36.dp).widthIn(min = 100.dp, max = 160.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (active) Color(0xFF30363A) else Color.Transparent)
                        .then(if (active) Modifier.border(1.dp, accent.copy(alpha = .55f), RoundedCornerShape(4.dp)) else Modifier)
                        .clickable { onWin(w) }.padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart,
                ) { Text(w.title, color = Breeze.Text, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            }
        }
        Row(Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            (0..3).forEach { i ->
                Box(Modifier.padding(horizontal = 3.dp).size(8.dp).clip(CircleShape)
                    .background(if (i == desk) Color(0xFF3DAEE9) else Color(0xFF64686B))
                    .clickable { onDesk(i) })
            }
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.padding(start = 8.dp, end = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf("notifications", "clipboard", "volume-muted", "brightness", "display", "arrow-up").forEach { icon ->
                BreezeIcon(icon, Modifier.padding(horizontal = 1.dp).size(18.dp), icon)
            }
            Column(Modifier.padding(start = 8.dp), horizontalAlignment = Alignment.End) {
                Text(now.format(DateTimeFormatter.ofPattern("h:mm a")), color = Breeze.Text, fontSize = 12.sp)
                Text(now.format(DateTimeFormatter.ofPattern("dd/MM/yy")), color = Breeze.Muted, fontSize = 9.sp)
            }
            Box(Modifier.padding(start = 10.dp).size(width = 18.dp, height = 28.dp).clickable { }, contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(13.dp)) { drawRect(Breeze.Muted, style = Stroke(1.dp.toPx())) }
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
    val selected = when {
        q.isNotBlank() -> all.filter { it.label.contains(q, true) }
        category == 0 -> {
            val recommended = Builtins + all.filter { app ->
                app.label.contains("firefox", true) || app.label.contains("browser", true) ||
                    app.label.contains("chrome", true) || app.label.contains("terminal", true) ||
                    app.label.contains("termux", true) || app.label.contains("text editor", true) ||
                    app.label.contains("kwrite", true) || app.label.contains("play store", true) ||
                    app.label.contains("app store", true) || app.label.contains("discover", true)
            }
            (recommended + favs.mapNotNull { p -> all.find { it.pkg == p } }).distinctBy { it.pkg }
        }
        category == 1 -> all
        else -> all.filter { app ->
            when (category) {
                2 -> app.label.contains("dev", true) || app.label.contains("code", true) || app.label.contains("terminal", true)
                3 -> app.label.contains("photo", true) || app.label.contains("camera", true) || app.label.contains("image", true)
                4 -> app.label.contains("help", true)
                5 -> app.label.contains("browser", true) || app.label.contains("chrome", true) || app.label.contains("firefox", true)
                6 -> app.label.contains("music", true) || app.label.contains("video", true)
                7 -> app.label.contains("office", true) || app.label.contains("docs", true)
                8 -> app.label.contains("settings", true) || app.label.contains("ajustes", true)
                else -> app.label.contains("file", true) || app.label.contains("archiv", true)
            }
        }
    }
    val shape = RoundedCornerShape(topEnd = 7.dp, topStart = 7.dp)
    Column(
        modifier.widthIn(max = 660.dp).fillMaxWidth().heightIn(max = 520.dp).height(520.dp).clip(shape)
            .background(Color(0xF21F2226)).border(1.dp, Color(0xFF45484C), shape)
            .pointerInput(Unit) { detectTapGestures { } }
            .padding(6.dp),
    ) {
        Row(Modifier.height(40.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(start = 2.dp).size(28.dp).clip(CircleShape).background(Color(0xFF26292D)).border(1.dp, accent, CircleShape), contentAlignment = Alignment.Center) {
                Text("SP", color = accent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
            BasicTextField(
                value = q, onValueChange = { q = it }, singleLine = true,
                textStyle = TextStyle(color = Breeze.Text, fontSize = 13.sp),
                cursorBrush = SolidColor(accent),
                modifier = Modifier.weight(1f).padding(start = 8.dp, end = 45.dp).height(30.dp)
                    .clip(RoundedCornerShape(4.dp)).background(Color(0xFF151719)).border(1.dp, Color(0xFF43474A), RoundedCornerShape(4.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
                decorationBox = { inner ->
                if (q.isEmpty()) Text("⌕  Search…", color = Breeze.Muted, fontSize = 13.sp)
                    inner()
                },
            )
        }
        Row(Modifier.weight(1f).fillMaxWidth()) {
            Column(Modifier.width(234.dp).fillMaxHeight().background(Color(0xDD292532)).padding(vertical = 5.dp)) {
                categories.forEachIndexed { i, t ->
                    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 1.dp).height(34.dp)
                        .clip(RoundedCornerShape(4.dp)).background(if (i == category) Color(0xFF17485C) else Color.Transparent)
                        .clickable { category = i; q = "" }.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        val categoryIcon = when (i) {
                            0 -> "favorites"
                            1 -> "category-all"
                            2 -> "category-development"
                            3 -> "category-graphics"
                            4 -> "help"
                            5 -> "category-internet"
                            6 -> "category-multimedia"
                            7 -> "category-office"
                            8 -> "category-system"
                            else -> "category-utilities"
                        }
                        BreezeIcon(categoryIcon, Modifier.width(24.dp).height(22.dp), t)
                        Text(t, color = if (i == category) Color.White else Breeze.Text, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (i == 1 || i == 4 || i == 7) Spacer(Modifier.height(3.dp))
                }
            }
            Column(Modifier.weight(1f).fillMaxHeight().padding(start = 8.dp, end = 4.dp, top = 4.dp)) {
                LazyVerticalGrid(GridCells.Adaptive(82.dp), Modifier.weight(1f)) {
                    items(selected) { a -> AppTile(a, a.pkg in favs, { onOpen(a) }, { onFav(a) }) }
                }
            }
        }
        Row(Modifier.fillMaxWidth().height(40.dp).background(Color(0xFF202327)).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("⊞  Applications", color = Breeze.Text, fontSize = 12.sp, modifier = Modifier.clickable { category = 1 }.padding(horizontal = 4.dp))
            Text("◉  Places", color = Breeze.Muted, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(Modifier.weight(1f))
            listOf("☾  Sleep", "⟳  Restart", "⏻  Shut Down", "‹  Session").forEach { action ->
                Text(action, color = Breeze.Text, fontSize = 10.sp, modifier = Modifier.clickable { }.padding(horizontal = 5.dp))
            }
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
