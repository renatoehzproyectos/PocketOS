package com.pocketos

import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
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
        // Immersive on every display: hides the Android status/gesture bars so only the Plasma shell shows.
        val c = WindowInsetsControllerCompat(window, window.decorView)
        c.hide(WindowInsetsCompat.Type.systemBars())
        c.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (secondary) Pointer.attach(window.decorView, display?.displayId ?: 0)
        setContent {
            val prefs by store.data.collectAsState(initial = emptyPreferences())
            val systemDensity = LocalDensity.current
            // Lay the shell out like the 1920px-wide Plasma reference on every display,
            // so sizes (655x516 menu, 52 px panel, 930 px windows) keep the same proportions.
            val dm = resources.displayMetrics
            val scale = (maxOf(dm.widthPixels, dm.heightPixels) / 1920f).coerceIn(0.5f, 4f)
            val shellDensity = Density(scale, 1f)
            PocketTheme(Color(prefs[Keys.ACCENT] ?: Accents[0])) {
                CompositionLocalProvider(LocalDensity provides shellDensity) {
                    Box(Modifier.fillMaxSize().onSizeChanged {
                        if (secondary) { Pointer.w = it.width.toFloat(); Pointer.h = it.height.toFloat() }
                    }) {
                        Desktop(prefs)
                        if (secondary && !Pointer.overlayOn) CursorOverlay()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (secondary) Pointer.desktopFocused = true
    }

    override fun onPause() {
        if (secondary) Pointer.desktopFocused = false
        super.onPause()
    }

    override fun onDestroy() {
        Pointer.detach(window.decorView)
        super.onDestroy()
    }
}
