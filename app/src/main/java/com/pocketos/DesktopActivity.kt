package com.pocketos

import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.preferences.core.emptyPreferences
import com.pocketos.core.input.Pointer
import com.pocketos.core.storage.Keys
import com.pocketos.core.storage.store
import com.pocketos.desktop.CursorOverlay
import com.pocketos.desktop.Desktop
import com.pocketos.design.Accents
import com.pocketos.design.PocketTheme

class DesktopActivity : ComponentActivity() {
    private var secondary = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        secondary = (display?.displayId ?: Display.DEFAULT_DISPLAY) != Display.DEFAULT_DISPLAY
        if (secondary) {
            val c = WindowInsetsControllerCompat(window, window.decorView)
            c.hide(WindowInsetsCompat.Type.systemBars())
            c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            Pointer.attach(window.decorView)
        }
        setContent {
            val prefs by store.data.collectAsState(initial = emptyPreferences())
            PocketTheme(Color(prefs[Keys.ACCENT] ?: Accents[0])) {
                Box(Modifier.fillMaxSize().onSizeChanged {
                    if (secondary) { Pointer.w = it.width.toFloat(); Pointer.h = it.height.toFloat() }
                }) {
                    Desktop(prefs)
                    if (secondary) CursorOverlay()
                }
            }
        }
    }

    override fun onDestroy() {
        Pointer.detach(window.decorView)
        super.onDestroy()
    }
}
