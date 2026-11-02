package com.pocketos

import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.pocketos.core.display.externalDisplay
import com.pocketos.core.display.launchDesktopOnExternal
import com.pocketos.design.Breeze
import com.pocketos.design.PocketTheme
import com.pocketos.shell.TouchpadScreen

class TouchpadActivity : ComponentActivity() {
    private var extName by mutableStateOf<String?>(null)

    private val listener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {
            refresh()
            launchDesktopOnExternal(this@TouchpadActivity)
        }
        override fun onDisplayRemoved(displayId: Int) { refresh() }
        override fun onDisplayChanged(displayId: Int) {}
    }

    private fun refresh() { extName = externalDisplay(this)?.name }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        getSystemService(DisplayManager::class.java)
            .registerDisplayListener(listener, Handler(Looper.getMainLooper()))
        refresh()
        setContent {
            PocketTheme(Breeze.Blue) {
                TouchpadScreen(
                    extName = extName,
                    onDesktopHere = { startActivity(Intent(this, DesktopActivity::class.java)) },
                    onRelaunch = { launchDesktopOnExternal(this, force = true) },
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
