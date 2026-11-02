package com.pocketos.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

val Context.store: DataStore<Preferences> by preferencesDataStore(name = "pocketos")

object Keys {
    val ACCENT = longPreferencesKey("accent")
    val WALL = intPreferencesKey("wall")
    val FAVS = stringPreferencesKey("favs")
    val RECENTS = stringPreferencesKey("recents")
}

fun Preferences.strList(key: Preferences.Key<String>): List<String> =
    (this[key] ?: "").split(",").filter { it.isNotBlank() }

suspend fun Context.setAccent(v: Long) { store.edit { it[Keys.ACCENT] = v } }
suspend fun Context.setWall(i: Int) { store.edit { it[Keys.WALL] = i } }

suspend fun Context.toggleFav(pkg: String) {
    store.edit {
        val l = it.strList(Keys.FAVS).toMutableList()
        if (!l.remove(pkg)) l.add(pkg)
        it[Keys.FAVS] = l.joinToString(",")
    }
}

suspend fun Context.pushRecent(pkg: String) {
    store.edit {
        val l = it.strList(Keys.RECENTS).toMutableList()
        l.remove(pkg)
        l.add(0, pkg)
        it[Keys.RECENTS] = l.take(12).joinToString(",")
    }
}
