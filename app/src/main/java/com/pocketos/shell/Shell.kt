package com.pocketos.shell

import android.os.BatteryManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
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
fun rememberBattery(): Int {
    val ctx = LocalContext.current
    val b by produceState(-1) {
        while (true) {
            value = ctx.getSystemService(BatteryManager::class.java)
                .getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            delay(30000)
        }
    }
    return b
}

@Composable
fun Taskbar(
    wins: List<Win>, desk: Int, focusedId: Int,
    onMenu: () -> Unit, onWin: (Win) -> Unit, onDesk: (Int) -> Unit,
) {
    val accent = LocalAccent.current
    val now = rememberNow()
    val bat = rememberBattery()
    Row(
        Modifier.fillMaxWidth().height(46.dp).background(Breeze.Panel),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.padding(horizontal = 8.dp).size(34.dp).clip(CircleShape)
                .background(accent).clickable(onClick = onMenu),
            contentAlignment = Alignment.Center,
        ) { Text("P", color = Color.White, fontWeight = FontWeight.Bold) }
        Row(Modifier.weight(1f).horizontalScroll(rememberScrollState())) {
            wins.filter { it.desk == desk }.forEach { w ->
                val active = w.id == focusedId && !w.minimized
                Box(
                    Modifier.padding(horizontal = 3.dp).height(36.dp).widthIn(min = 100.dp, max = 160.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (active) Breeze.Surface else Color.Transparent)
                        .border(1.dp, if (active) accent else Breeze.Border, RoundedCornerShape(4.dp))
                        .clickable { onWin(w) }.padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart,
                ) { Text(w.title, color = Breeze.Text, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            }
        }
        Row {
            (0..3).forEach { i ->
                Box(
                    Modifier.padding(2.dp).size(26.dp).clip(RoundedCornerShape(4.dp))
                        .background(if (i == desk) accent else Breeze.Surface).clickable { onDesk(i) },
                    contentAlignment = Alignment.Center,
                ) { Text("${i + 1}", color = Color.White, fontSize = 12.sp) }
            }
        }
        Column(Modifier.padding(horizontal = 10.dp), horizontalAlignment = Alignment.End) {
            Text(now.format(DateTimeFormatter.ofPattern("HH:mm")), color = Breeze.Text, fontSize = 14.sp)
            Text(
                now.format(DateTimeFormatter.ofPattern("dd/MM/yy")) + if (bat >= 0) "  🔋$bat%" else "",
                color = Breeze.Muted, fontSize = 10.sp,
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
            else Text(app.glyph, fontSize = 30.sp)
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
    var tab by remember { mutableIntStateOf(0) }
    var q by remember { mutableStateOf("") }
    val all = Builtins + apps
    val shown = if (q.isNotBlank()) all.filter { it.label.contains(q, true) }
    else when (tab) {
        0 -> (Builtins + favs.mapNotNull { p -> all.find { it.pkg == p } }).distinctBy { it.pkg }
        1 -> all
        else -> recents.mapNotNull { p -> all.find { it.pkg == p } }
    }
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier.widthIn(max = 440.dp).fillMaxWidth().height(520.dp).clip(shape)
            .background(Breeze.Panel).border(1.dp, Breeze.Border, shape)
            .pointerInput(Unit) { detectTapGestures { } }
            .padding(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(30.dp).clip(CircleShape).background(accent), contentAlignment = Alignment.Center) {
                Text("P", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text("  PocketOS", color = Breeze.Text, fontSize = 15.sp)
        }
        Spacer(Modifier.height(8.dp))
        BasicTextField(
            value = q, onValueChange = { q = it }, singleLine = true,
            textStyle = TextStyle(color = Breeze.Text, fontSize = 14.sp),
            cursorBrush = SolidColor(accent),
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(Breeze.Bg).padding(10.dp),
            decorationBox = { inner ->
                if (q.isEmpty()) Text("Buscar…", color = Breeze.Muted, fontSize = 14.sp)
                inner()
            },
        )
        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            listOf("Favoritos", "Todas", "Recientes").forEachIndexed { i, t ->
                Text(
                    t, color = if (i == tab) accent else Breeze.Muted, fontSize = 13.sp,
                    modifier = Modifier.weight(1f).clickable { tab = i; q = "" }.padding(8.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
        LazyVerticalGrid(GridCells.Adaptive(84.dp), Modifier.weight(1f)) {
            items(shown) { a -> AppTile(a, a.pkg in favs, { onOpen(a) }, { onFav(a) }) }
        }
        Text("Mantén pulsado para fijar/quitar favorito", color = Breeze.Muted, fontSize = 10.sp)
    }
}

@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    val now = rememberNow()
    val bat = rememberBattery()
    Column(
        modifier.clip(RoundedCornerShape(10.dp)).background(Color(0x99232629)).padding(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Text(now.format(DateTimeFormatter.ofPattern("HH:mm")), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Light)
        Text(now.format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy")), color = Breeze.Muted, fontSize = 12.sp)
        if (bat >= 0) Text("🔋 $bat%", color = Breeze.Muted, fontSize = 12.sp)
    }
}

@Composable
fun DesktopIcon(glyph: String, label: String, onClick: () -> Unit) {
    Column(
        Modifier.width(76.dp).clip(RoundedCornerShape(6.dp)).clickable(onClick = onClick).padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(glyph, fontSize = 32.sp)
        Text(label, color = Color.White, fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}
