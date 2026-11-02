package com.pocketos.core.window

import androidx.compose.runtime.*

class Win(
    val id: Int, val kind: String, val title: String, val desk: Int,
    x0: Float, y0: Float, w0: Float, h0: Float,
) {
    var x by mutableFloatStateOf(x0)
    var y by mutableFloatStateOf(y0)
    var w by mutableFloatStateOf(w0)
    var h by mutableFloatStateOf(h0)
    var minimized by mutableStateOf(false)
    var maximized by mutableStateOf(false)
    var z by mutableIntStateOf(0)
}

class WindowManager {
    val wins = mutableStateListOf<Win>()
    var focusedId by mutableIntStateOf(-1)
    private var next = 1
    private var zc = 0

    fun open(kind: String, title: String, desk: Int, areaW: Float, areaH: Float) {
        wins.find { it.kind == kind && it.desk == desk }?.let { focus(it); return }
        val n = wins.count { it.desk == desk }
        val w = Win(
            next++, kind, title, desk,
            24f + n * 28, 16f + n * 28,
            minOf(560f, areaW - 32f), minOf(380f, areaH - 32f),
        )
        wins.add(w)
        focus(w)
    }

    fun focus(w: Win) { w.minimized = false; w.z = ++zc; focusedId = w.id }
    fun close(w: Win) { wins.remove(w) }

    fun fromTaskbar(w: Win) {
        if (focusedId == w.id && !w.minimized) w.minimized = true else focus(w)
    }
}
