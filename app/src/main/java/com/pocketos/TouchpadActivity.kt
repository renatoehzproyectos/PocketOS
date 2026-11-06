package com.pocketos

import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.pocketos.core.display.Diag
import com.pocketos.core.display.externalDisplay
import com.pocketos.core.display.launchDesktopOnExternal
import com.pocketos.core.display.resetLaunchAttempts
import com.pocketos.core.input.Pointer
import com.pocketos.core.input.Remote
import com.pocketos.design.Breeze
import com.pocketos.design.PocketTheme
import com.pocketos.shell.TouchpadScreen
import com.pocketos.system.PocketAccessibilityService
import kotlinx.coroutines.delay

class TouchpadActivity : ComponentActivity() {
    private var extName by mutableStateOf<String?>(null)
    private var a11yOn by mutableStateOf(false)

    private val listener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {
            Diag.d("Pantalla añadida: id $displayId")
            resetLaunchAttempts()
            refresh()
            launchDesktopOnExternal(this@TouchpadActivity)
        }
        override fun onDisplayRemoved(displayId: Int) {
            Diag.d("Pantalla quitada: id $displayId")
            refresh()
        }
        override fun onDisplayChanged(displayId: Int) {}
    }

    private fun refresh() {
        extName = externalDisplay(this)?.name
        a11yOn = PocketAccessibilityService.instance != null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.pocketos.vd.VdHost.init(applicationContext)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        getSystemService(DisplayManager::class.java)
            .registerDisplayListener(listener, Handler(Looper.getMainLooper()))
        refresh()
        setContent {
            LaunchedEffect(Unit) {
                while (true) {
                    refresh()
                    if (extName != null) launchDesktopOnExternal(this@TouchpadActivity)
                    delay(3000)
                }
            }
            PocketTheme(Breeze.Blue) {
                TouchpadScreen(
                    extName = extName,
                    a11yOn = a11yOn,
                    onDesktopHere = { startActivity(Intent(this, DesktopActivity::class.java)) },
                    onRelaunch = { launchDesktopOnExternal(this, force = true) },
                    onHome = {
                        if (Pointer.desktopFocused || !Pointer.active) Remote.send("home")
                        else launchDesktopOnExternal(this, force = true)
                    },
                    onOpenA11y = { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
                    onClose = { finish() },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
        launchDesktopOnExternal(this)
    }

    override fun onDestroy() {
        getSystemService(DisplayManager::class.java).unregisterDisplayListener(listener)
        super.onDestroy()
    }
}
