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

    Row(Modifier.fillMaxSize().background(Color(0xFF141618))) {
        Column(Modifier.width(270.dp).fillMaxHeight().background(Color(0xFF1E2124))) {
            Row(Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("‹", color = Breeze.Text, fontSize = 25.sp)
                Text("Colors & Themes", color = Breeze.Text, fontSize = 15.sp, modifier = Modifier.weight(1f).padding(start = 8.dp))
                Text("☰", color = Breeze.Text, fontSize = 17.sp)
            }
            cats.forEachIndexed { i, c ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 1.dp).height(31.dp)
                    .clip(RoundedCornerShape(5.dp)).background(if (i == cat) Color(0xFF17485C) else Color.Transparent)
                    .clickable { cat = i }.padding(horizontal = 7.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    val settingIcon = when (i) {
                        0 -> "settings-global"
                        1 -> "settings-colors"
                        2 -> "settings-applications"
                        3 -> "settings-plasma"
                        4 -> "settings-window"
                        else -> "settings-icons"
                    }
                    BreezeIcon(settingIcon, Modifier.width(25.dp).height(22.dp), c)
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
                                Box(Modifier.size(110.dp, 64.dp).clip(RoundedCornerShape(5.dp)).background(Brush.linearGradient(colors))
                                    .border(if (i == curWall) 2.dp else 1.dp, if (i == curWall) Color.White else Breeze.Border, RoundedCornerShape(5.dp))
                                    .clickable { onWall(i) })
                            }
                        }
                    }
                    2 -> Text("Application style\n\nBreeze  ·  Fusion", color = Breeze.Text, fontSize = 14.sp)
                    3 -> Text("Plasma style\n\nBreeze", color = Breeze.Text, fontSize = 14.sp)
                    4 -> Text("Window decorations\n\nBreeze", color = Breeze.Text, fontSize = 14.sp)
                    else -> Text("Icons\n\nBreeze", color = Breeze.Text, fontSize = 14.sp)
                }
            }
            Row(Modifier.fillMaxWidth().height(54.dp).background(Color(0xFF1D2023)).padding(horizontal = 12.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    BreezeIcon("help", Modifier.size(16.dp), "Help")
                    androidx.compose.material3.OutlinedButton(onClick = {}) { Text("Help", color = Breeze.Text, fontSize = 12.sp) }
                }
                Spacer(Modifier.width(8.dp))
                androidx.compose.material3.OutlinedButton(onClick = { theme = 1 }) { Text("Defaults", color = Breeze.Text, fontSize = 12.sp) }
            }
        }
    }
}

@Composable
private fun ThemeCard(name: String, index: Int, selected: Boolean, onClick: () -> Unit) {
    Column(Modifier.width(174.dp).clickable(onClick = onClick), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().height(106.dp).clip(RoundedCornerShape(5.dp))
            .background(if (selected) Color(0xFF293A42) else Color(0xFF101214))
            .border(if (selected) 2.dp else 1.dp, if (selected) Color(0xFF3DAEE9) else Color(0xFF303438), RoundedCornerShape(5.dp))) {
            Canvas(Modifier.fillMaxSize()) {
                val wallpaper = when (index) {
                    0 -> listOf(Color(0xFFEA8D9B), Color(0xFF4156A4), Color(0xFF347DBC))
                    1 -> listOf(Color(0xFF522179), Color(0xFF1C102E), Color(0xFFE45E90))
                    2 -> listOf(Color(0xFF423466), Color(0xFF17263E), Color(0xFF436B92))
                    else -> listOf(Color(0xFF792E93), Color(0xFF33235F), Color(0xFF3687C5))
                }
                drawRect(Brush.linearGradient(wallpaper))
                val panel = if (index == 0) Color(0xFFF3F3F3) else Color(0xFF202326)
                drawRect(panel, topLeft = androidx.compose.ui.geometry.Offset(0f, size.height * .88f), size = androidx.compose.ui.geometry.Size(size.width, size.height * .12f))
                drawRoundRect(if (index == 0) Color(0xFFF9F9F9) else Color(0xFF191B1D),
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * .36f, size.height * .12f),
                    size = androidx.compose.ui.geometry.Size(size.width * .58f, size.height * .64f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()))
                drawRect(Color(0xFF303438), topLeft = androidx.compose.ui.geometry.Offset(size.width * .36f, size.height * .12f), size = androidx.compose.ui.geometry.Size(size.width * .58f, size.height * .1f))
            }
        }
        Text(name, color = if (selected) Color.White else Breeze.Text, fontSize = 13.sp,
            fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
            modifier = Modifier.padding(top = 8.dp))
    }
}
