package com.pocketos.design

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.RectF
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.caverock.androidsvg.SVG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

private val breezeBitmapCache = ConcurrentHashMap<String, ImageBitmap>()

/** Renders the real Breeze SVG assets to a small cached bitmap for Compose. */
@Composable
fun BreezeIcon(name: String, modifier: Modifier = Modifier, description: String? = null) {
    val context = LocalContext.current
    val bitmapState = produceState<ImageBitmap?>(null, name) {
        value = breezeBitmapCache[name] ?: withContext(Dispatchers.IO) {
            runCatching {
                val svg = SVG.getFromAsset(context.assets, "breeze/$name.svg")
                val picture = svg.renderToPicture()
                val bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888)
                val canvas = AndroidCanvas(bitmap)
                canvas.drawPicture(picture, RectF(0f, 0f, 96f, 96f))
                bitmap.asImageBitmap().also { breezeBitmapCache.putIfAbsent(name, it) }
            }.getOrNull()
        }
    }
    bitmapState.value?.let { Image(it, description, modifier, contentScale = ContentScale.Fit) }
}
