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
import androidx.compose.ui.draw.clipToBounds
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
import androidx.compose.ui.unit.IntSize
import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.Frame
import io.github.fourlastor.data.Transform
import io.github.fourlastor.editor.state.EntityNode
import io.github.fourlastor.editor.state.GroupNode
import io.github.fourlastor.editor.state.ImageNode
import io.github.fourlastor.editor.state.toEntitiesState
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
    entities: Entities,
    modifier: Modifier,
) {
    val entityPreview by remember(entities) {
        derivedStateOf {
            entities
                .toEntitiesState()
                .asNode()
                .toPreview()
        }
    }
    var pan by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = modifier
            .clipToBounds()
            .onDrag(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
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

/** Transforms [this@toPreview] to a version that can be displayed, loading images and so forth. */
private fun EntityNode.toPreview(): EntityPreview = when (this) {
    is GroupNode -> GroupPreview(
        entities = children.map { it.toPreview() }.toImmutableList(),
        transform = entity.transform,
    )

    is ImageNode -> ImagePreview(
        image = loadImageFromPath(entity.path),
        transform = entity.transform,
        frame = entity.frame,
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
    val frame: Frame,
) : EntityPreview() {

    val Frame.maxNumberFrames: Int
        get() {
            return columns * rows
        }
    val Frame.frameNumberAdjusted: Int
        get() {
            return frameNumber % maxNumberFrames
        }
    val Frame.width: Int
        get() {
            return image.width / columns
        }
    val Frame.left: Int
        get() {
            return width * (frameNumberAdjusted % columns)
        }
    val Frame.right: Int
        get() {
            return left + width
        }
    val Frame.height: Int
        get() {
            return image.height / rows
        }
    val Frame.top: Int
        get() {
            return height * (frameNumberAdjusted / columns)
        }
    val Frame.bottom: Int
        get() {
            return top + height
        }

    override fun draw(drawScope: DrawScope) = drawScope.withTransform(transform.action) {
        drawImage(
            image = image,
            srcOffset = IntOffset(frame.left, frame.top),
            srcSize = IntSize(frame.width, frame.height),
            dstOffset = intCenter - image.center,
            filterQuality = FilterQuality.None
        )
    }
}


val Transform.action: DrawTransform.() -> Unit
    get() = {
        translate(x, y)
        if (rotation != 0f) {
            rotate(rotation)
        }
        scale(scale)
    }

private val DrawScope.intCenter: IntOffset
    get() = IntOffset(center.x.toInt(), center.y.toInt())
private val ImageBitmap.center: IntOffset
    get() = IntOffset(width / 2, height / 2)
