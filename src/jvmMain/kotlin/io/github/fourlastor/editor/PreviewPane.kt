package io.github.fourlastor.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.IntOffset

@Composable
fun PreviewPane(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
    ) {
        var zoom by remember { mutableStateOf(1f) }
        val bmp = remember { useResource("player.png") { loadImageBitmap(it) } }
        Canvas(modifier = Modifier.matchParentSize()) {
            scale(zoom * 20) {
                drawImage(
                    image = bmp,
                    dstOffset = intCenter - bmp.center,
                    filterQuality = FilterQuality.None
                )
            }
        }

        Slider(
            value = zoom,
            modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth(0.4f),
            onValueChange = { zoom = it }
        )
    }
}

private val DrawScope.intCenter: IntOffset
    get() = IntOffset(center.x.toInt(), center.y.toInt())
private val ImageBitmap.center: IntOffset
    get() = IntOffset(width / 2, height / 2)
