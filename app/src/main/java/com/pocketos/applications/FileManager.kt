package com.pocketos.applications

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pocketos.design.Breeze
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileManager() {
    val ctx = LocalContext.current
    val root = remember { Environment.getExternalStorageDirectory() }
    var dir by remember { mutableStateOf(root) }
    var tick by remember { mutableIntStateOf(0) }
    var toDelete by remember { mutableStateOf<File?>(null) }
    var newName by remember { mutableStateOf<String?>(null) }
    val entries = remember(dir, tick) {
        dir.listFiles()?.sortedWith(compareBy<File>({ !it.isDirectory }, { it.name.lowercase() }))
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(Breeze.Panel).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton({ if (dir != root) dir = dir.parentFile ?: dir }) { Text("↑") }
            TextButton({ tick++ }) { Text("⟳") }
            TextButton({ newName = "" }) { Text("＋📁") }
            TextButton({
                try {
                    ctx.startActivity(
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + ctx.packageName))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } catch (_: Exception) {}
            }) { Text("🔑") }
            Text(dir.path, color = Breeze.Muted, fontSize = 11.sp, maxLines = 1)
        }
        if (entries == null) {
            Text("Sin acceso. Pulsa 🔑 y concede «Acceso a todos los archivos», luego ⟳.", color = Breeze.Text, modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(entries) { f ->
                    Row(
                        Modifier.fillMaxWidth()
                            .combinedClickable(onClick = { if (f.isDirectory) dir = f }, onLongClick = { toDelete = f })
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(if (f.isDirectory) "📁" else "📄", fontSize = 18.sp)
                        Text("  " + f.name, color = Breeze.Text, fontSize = 13.sp, modifier = Modifier.weight(1f), maxLines = 1)
                        if (f.isFile) Text("${f.length() / 1024} KB", color = Breeze.Muted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
    toDelete?.let { f ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Eliminar") },
            text = { Text("¿Eliminar «${f.name}»?") },
            confirmButton = { TextButton({ f.deleteRecursively(); toDelete = null; tick++ }) { Text("Eliminar") } },
            dismissButton = { TextButton({ toDelete = null }) { Text("Cancelar") } },
        )
    }
    newName?.let { n ->
        AlertDialog(
            onDismissRequest = { newName = null },
            title = { Text("Nueva carpeta") },
            text = { TextField(n, { newName = it }, singleLine = true) },
            confirmButton = {
                TextButton({
                    if (n.isNotBlank()) File(dir, n).mkdirs()
                    newName = null; tick++
                }) { Text("Crear") }
            },
            dismissButton = { TextButton({ newName = null }) { Text("Cancelar") } },
        )
    }
}
