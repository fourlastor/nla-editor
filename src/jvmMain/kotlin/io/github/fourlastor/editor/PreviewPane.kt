package io.github.fourlastor.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.IntOffset
import io.github.fourlastor.entity.EntityNode
import io.github.fourlastor.entity.GroupNode
import io.github.fourlastor.entity.ImageNode
import io.github.fourlastor.entity.Transform

@ExperimentalFoundationApi
@Composable
fun PreviewPane(
    state: EditorState,
    modifier: Modifier,
) {
    var pan by remember { mutableStateOf(Offset.Zero) }
    val entityPreview by remember { derivedStateOf { toPreview(state.entities.asNode()) } }
    Box(
        modifier = modifier.onDrag(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
            pan += it
        }
    ) {
        var zoom by remember { mutableStateOf(1f) }
        Canvas(modifier = Modifier.matchParentSize()) {
            withTransform({
                translate(pan.x, pan.y)
                scale(zoom * 20)
            }) {
                entityPreview.draw(this)
            }
        }

//        Slider(
//            value = zoom,
//            modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth(0.4f),
//            onValueChange = { zoom = it }
//        )
    }
}

private fun toPreview(entity: EntityNode): EntityPreview = when (entity) {
    is GroupNode -> GroupPreview(
        entities = entity.children.map { toPreview(it) },
        transform = entity.entity.transform,
    )

    is ImageNode -> ImagePreview(
        image = useResource(entity.entity.path) { loadImageBitmap(it) },
        transform = entity.entity.transform,
    )
}

private sealed interface EntityPreview {
    fun draw(drawScope: DrawScope)
}

private data class GroupPreview(
    val entities: List<EntityPreview>,
    val transform: Transform,
) : EntityPreview {

    override fun draw(drawScope: DrawScope) = drawScope.withTransform(transform.action) {
        for (entity in entities) {
            entity.draw(this)
        }
    }
}

private data class ImagePreview(
    val transform: Transform,
    val image: ImageBitmap,
) : EntityPreview {
    override fun draw(drawScope: DrawScope) = drawScope.withTransform(transform.action) {
        drawImage(
            image = image,
            dstOffset = intCenter - image.center,
            filterQuality = FilterQuality.None
        )
    }
}

private val DrawScope.intCenter: IntOffset
    get() = IntOffset(center.x.toInt(), center.y.toInt())
private val ImageBitmap.center: IntOffset
    get() = IntOffset(width / 2, height / 2)
