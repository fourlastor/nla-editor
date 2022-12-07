package io.github.fourlastor.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.IntOffset
import io.github.fourlastor.editor.state.EntitiesState
import io.github.fourlastor.editor.state.EntityNode
import io.github.fourlastor.editor.state.GroupNode
import io.github.fourlastor.editor.state.ImageNode
import io.github.fourlastor.entity.Transform
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.io.File

/**
 * Displays a preview of the current project.
 * Displays the images in a canvas, and properly parents entities so that their parent transform applies to the children.
 */
@ExperimentalFoundationApi
@Composable
fun PreviewPane(
        entities: EntitiesState,
        modifier: Modifier,
) {
    var pan by remember { mutableStateOf(Offset.Zero) }
    val entityPreview by remember(entities) { derivedStateOf { toPreview(entities.asNode()) } }
    Box(
        modifier = modifier.onDrag(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
            pan += it
        }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            withTransform({
                translate(pan.x, pan.y)
                scale(20f)
            }) {
                entityPreview.draw(this)
            }
        }
    }
}

/** Transforms [entity] to a version that can be displayed, loading images and so forth. */
private fun toPreview(entity: EntityNode): EntityPreview = when (entity) {
    is GroupNode -> GroupPreview(
        entities = entity.children.map { toPreview(it) }.toImmutableList(),
        transform = entity.entity.transform,
    )

    is ImageNode -> ImagePreview(
        image = loadImageFromPath(entity.entity.path),
        transform = entity.entity.transform,
    )
}

private fun loadImageFromPath(path: String): ImageBitmap =
    File(path).inputStream().buffered().use(::loadImageBitmap)

private sealed class EntityPreview {
    abstract fun draw(drawScope: DrawScope)
}

private data class GroupPreview(
    val entities: ImmutableList<EntityPreview>,
    val transform: Transform,
) : EntityPreview() {

    override fun draw(drawScope: DrawScope) = drawScope.withTransform(transform.action) {
        for (entity in entities) {
            entity.draw(this)
        }
    }
}

private data class ImagePreview(
    val transform: Transform,
    val image: ImageBitmap,
) : EntityPreview() {
    override fun draw(drawScope: DrawScope) = drawScope.withTransform(transform.action) {
        drawImage(
            image = image,
            dstOffset = intCenter - image.center,
            filterQuality = FilterQuality.None
        )
    }
}


val Transform.action: DrawTransform.() -> Unit
    get() = {
        translate(translation.x, translation.y)
        if (rotation != 0f) {
            rotate(rotation)
        }
        scale(scale)
    }

private val DrawScope.intCenter: IntOffset
    get() = IntOffset(center.x.toInt(), center.y.toInt())
private val ImageBitmap.center: IntOffset
    get() = IntOffset(width / 2, height / 2)
