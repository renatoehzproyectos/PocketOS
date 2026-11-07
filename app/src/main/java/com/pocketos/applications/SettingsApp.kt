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
import com.pocketos.vd.VdHost
import com.pocketos.design.BreezeIcon
import com.pocketos.design.Wallpapers
import com.pocketos.design.WallpaperRes

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsApp(prefs: Preferences, onAccent: (Long) -> Unit, onWall: (Int) -> Unit) {
    var cat by remember { mutableIntStateOf(0) }
    val cats = listOf("Colors", "Window Decorations")
    val curAccent = prefs[Keys.ACCENT] ?: Accents[0]
    val curWall = prefs[Keys.WALL] ?: 0

    Row(Modifier.fillMaxSize().background(Color(0xC71A1A20))) {
        Column(Modifier.width(270.dp).fillMaxHeight().background(Color(0xB3222229))) {
            Row(Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Colors & Themes", color = Breeze.Text, fontSize = 15.sp, modifier = Modifier.weight(1f))
            }
            cats.forEachIndexed { i, c ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 1.dp).height(31.dp)
                    .clip(RoundedCornerShape(4.dp)).background(if (i == cat) Color(0xFF2B4380) else Color.Transparent)
                    .then(if (i == cat) Modifier.border(1.dp, Color(0xFF3B69C7), RoundedCornerShape(4.dp)) else Modifier)
                    .clickable { cat = i }.padding(horizontal = 7.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    val settingIcon = if (i == 0) "settings-colors" else "settings-window"
                    BreezeIcon(settingIcon, Modifier.size(22.dp), c)
                    Spacer(Modifier.width(7.dp))
                    Text(c, color = if (i == cat) Color.White else Breeze.Text, fontSize = 13.sp)
                }
            }
        }
        Column(Modifier.weight(1f).fillMaxHeight()) {
            Row(Modifier.fillMaxWidth().height(54.dp).padding(start = 18.dp, end = 14.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(cats[cat], color = Breeze.Text, fontSize = 19.sp, maxLines = 1, softWrap = false, modifier = Modifier.weight(1f))
            }
            Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
                when (cat) {
                    0 -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
                                    .then(if (WallpaperRes[i] != null) Modifier else Modifier.background(Brush.linearGradient(colors)))
                                    .border(if (i == curWall) 2.dp else 1.dp, if (i == curWall) Color.White else Breeze.Border, RoundedCornerShape(5.dp))
                                    .clickable { onWall(i) }) {
                                    WallpaperRes[i]?.let { res -> Image(painterResource(res), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
                                }
                            }
                        }
                    }
                    else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Hosted app windows (Shizuku)", color = Breeze.Text, fontSize = 14.sp)
                        Text(VdHost.status, color = if (VdHost.ready) Color(0xFF27AE60) else Breeze.Muted, fontSize = 12.sp)
                        BreezeButton(if (VdHost.ready) "Connected" else "Connect", "defaults") { VdHost.requestPermission() }
                        Text("Needs Shizuku running and Android 13+. See VIRTUAL_DISPLAY_SETUP.md.", color = Breeze.Muted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BreezeButton(label: String, icon: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    Row(
        Modifier.height(30.dp).clip(shape).background(Color(0xFF34343C)).border(1.dp, Color(0xFF4A4A54), shape)
            .clickable(onClick = onClick).padding(horizontal = 10.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        BreezeIcon(icon, Modifier.size(16.dp), label)
        Spacer(Modifier.width(6.dp))
        Text(label, color = Breeze.Text, fontSize = 12.sp, maxLines = 1)
    }
}
