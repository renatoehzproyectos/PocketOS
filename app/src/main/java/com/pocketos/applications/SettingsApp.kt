package com.pocketos.applications

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import com.pocketos.core.storage.Keys
import com.pocketos.design.Accents
import com.pocketos.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.pocketos.design.Breeze
import com.pocketos.design.BreezeIcon
import com.pocketos.design.Wallpapers

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsApp(prefs: Preferences, onAccent: (Long) -> Unit, onWall: (Int) -> Unit) {
    var cat by remember { mutableIntStateOf(0) }
    var theme by remember { mutableIntStateOf(1) }
    val cats = listOf("Global Theme", "Colors", "Application Style", "Plasma Style", "Window Decorations", "Icons")
    val curAccent = prefs[Keys.ACCENT] ?: Accents[0]
    val curWall = prefs[Keys.WALL] ?: 0

    Row(Modifier.fillMaxSize().background(Color(0xB8141618))) {
        Column(Modifier.width(270.dp).fillMaxHeight().background(Color(0xB31E2124))) {
            Row(Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("‹", color = Breeze.Text, fontSize = 25.sp)
                Text("Colors & Themes", color = Breeze.Text, fontSize = 15.sp, modifier = Modifier.weight(1f).padding(start = 8.dp))
                Text("☰", color = Breeze.Text, fontSize = 17.sp)
            }
            cats.forEachIndexed { i, c ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 1.dp).height(31.dp)
                    .clip(RoundedCornerShape(4.dp)).background(if (i == cat) Color(0xFF1D4A5F) else Color.Transparent)
                    .then(if (i == cat) Modifier.border(1.dp, Color(0xFF3DAEE9), RoundedCornerShape(4.dp)) else Modifier)
                    .clickable { cat = i }.padding(start = if (i == 0) 7.dp else 25.dp, end = 7.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    val settingIcon = when (i) {
                        0 -> "settings-global"
                        1 -> "settings-colors"
                        2 -> "settings-applications"
                        3 -> "settings-plasma"
                        4 -> "settings-window"
                        else -> "settings-icons"
                    }
                    BreezeIcon(settingIcon, Modifier.size(22.dp), c)
                    Spacer(Modifier.width(7.dp))
                    Text(c, color = if (i == cat) Color.White else Breeze.Text, fontSize = 13.sp)
                }
            }
        }
        Column(Modifier.weight(1f).fillMaxHeight()) {
            Row(Modifier.fillMaxWidth().height(54.dp).padding(start = 18.dp, end = 14.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(if (cat == 0) "Global Theme" else cats[cat], color = Breeze.Text, fontSize = 19.sp, maxLines = 1, softWrap = false, modifier = Modifier.weight(1f))
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    BreezeIcon("get-new", Modifier.size(16.dp), "Get New")
                    Text("Get New…", color = Breeze.Text, fontSize = 12.sp)
                }
            }
            Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
                when (cat) {
                    0 -> FlowRow(horizontalArrangement = Arrangement.spacedBy(26.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        listOf("Breeze", "Breeze Dark", "Breeze Twilight", "Fedora").forEachIndexed { i, name ->
                            ThemeCard(name, i, theme == i) { theme = i }
                        }
                    }
                    1 -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Accent color", color = Breeze.Text, fontSize = 14.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Accents.forEach { c ->
                                Box(Modifier.size(32.dp).clip(CircleShape).background(Color(c))
                                    .border(if (c == curAccent) 3.dp else 0.dp, Color.White, CircleShape).clickable { onAccent(c) })
                            }
                        }
                        Text("Desktop wallpaper", color = Breeze.Text, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Wallpapers.forEachIndexed { i, colors ->
                                Box(Modifier.size(110.dp, 64.dp).clip(RoundedCornerShape(5.dp))
                                    .then(if (i == 0) Modifier else Modifier.background(Brush.linearGradient(colors)))
                                    .border(if (i == curWall) 2.dp else 1.dp, if (i == curWall) Color.White else Breeze.Border, RoundedCornerShape(5.dp))
                                    .clickable { onWall(i) }) {
                                    if (i == 0) Image(painterResource(R.drawable.wallpaper_plasma), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                }
                            }
                        }
                    }
                    2 -> Text("Application style\n\nBreeze  ·  Fusion", color = Breeze.Text, fontSize = 14.sp)
                    3 -> Text("Plasma style\n\nBreeze", color = Breeze.Text, fontSize = 14.sp)
                    4 -> Text("Window decorations\n\nBreeze", color = Breeze.Text, fontSize = 14.sp)
                    else -> Text("Icons\n\nBreeze", color = Breeze.Text, fontSize = 14.sp)
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FCFCFC)))
            Row(Modifier.fillMaxWidth().height(52.dp).background(Color(0xB3232629)).padding(horizontal = 10.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                BreezeButton("Help", "help") {}
                Spacer(Modifier.width(6.dp))
                BreezeButton("Defaults", "defaults") { theme = 1 }
            }
        }
    }
}

@Composable
private fun BreezeButton(label: String, icon: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    Row(
        Modifier.height(30.dp).clip(shape).background(Color(0xFF31363B)).border(1.dp, Color(0xFF4D5358), shape)
            .clickable(onClick = onClick).padding(horizontal = 10.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        BreezeIcon(icon, Modifier.size(16.dp), label)
        Spacer(Modifier.width(6.dp))
        Text(label, color = Breeze.Text, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
private fun ThemeCard(name: String, index: Int, selected: Boolean, onClick: () -> Unit) {
    Column(Modifier.width(174.dp).clickable(onClick = onClick), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().height(106.dp).clip(RoundedCornerShape(5.dp))
            .background(if (selected) Color(0xFF293A42) else Color(0xFF101214))
            .border(if (selected) 2.dp else 1.dp, if (selected) Color(0xFF3DAEE9) else Color(0xFF303438), RoundedCornerShape(5.dp))) {
            Canvas(Modifier.fillMaxSize()) {
                val light = index == 0
                val wall = when (index) {
                    0 -> listOf(Color(0xFFEA8D9B), Color(0xFF4156A4), Color(0xFF347DBC))
                    1 -> listOf(Color(0xFF522179), Color(0xFF1C102E), Color(0xFFE45E90))
                    2 -> listOf(Color(0xFF423466), Color(0xFF17263E), Color(0xFF436B92))
                    else -> listOf(Color(0xFF792E93), Color(0xFF33235F), Color(0xFF3687C5))
                }
                val o = { x: Float, y: Float -> androidx.compose.ui.geometry.Offset(size.width * x, size.height * y) }
                val sz = { w: Float, h: Float -> androidx.compose.ui.geometry.Size(size.width * w, size.height * h) }
                val r = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx())
                drawRect(Brush.linearGradient(wall))
                // panel
                drawRect(if (light) Color(0xFFEFF0F1) else Color(0xFF1E2124), o(0f, .92f), sz(1f, .08f))
                // back window (file manager)
                val winBg = if (light) Color(0xFFFCFCFC) else Color(0xFF1B1E20)
                val bar = if (light) Color(0xFFDDE0E3) else Color(0xFF2A2E32)
                val txt = if (light) Color(0xFF9AA0A5) else Color(0xFF50565B)
                drawRoundRect(winBg, o(.06f, .14f), sz(.62f, .70f), r)
                drawRect(bar, o(.06f, .14f), sz(.62f, .07f))
                drawRect(if (light) Color(0xFFEFF0F1) else Color(0xFF232629), o(.06f, .21f), sz(.14f, .63f))
                for (row in 0..2) for (col in 0..3) drawRect(Color(0xFF3DAEE9), o(.24f + col * .10f, .27f + row * .15f), sz(.055f, .075f))
                // front window (text editor / settings)
                drawRoundRect(winBg, o(.40f, .36f), sz(.54f, .50f), r)
                drawRect(bar, o(.40f, .36f), sz(.54f, .07f))
                for (row in 0..4) drawRect(txt, o(.44f, .47f + row * .075f), sz(if (row % 2 == 0) .40f else .28f, .018f))
                drawRect(Color(0xFF3DAEE9), o(.80f, .47f), sz(.09f, .09f))
            }
        }
        Text(name, color = if (selected) Color.White else Breeze.Text, fontSize = 13.sp,
            fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
            modifier = Modifier.padding(top = 8.dp))
    }
}
