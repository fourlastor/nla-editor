package io.github.fourlastor.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset

sealed interface Entity {
    fun draw(drawScope: DrawScope)

    val transform: Transform
}

data class Group(
    val entities: List<Entity>,
    override val transform: Transform = Transform.IDENTITY,
) : Entity {
    override fun draw(drawScope: DrawScope) = drawScope.withTransform(transform.action) {
        for (entity in entities) {
            entity.draw(this)
        }
    }
}

data class Image(
    val image: ImageBitmap,
    override val transform: Transform = Transform.IDENTITY,
) : Entity {
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


data class Transform(
    val offset: Offset,
    val rotation: Float,
    val scale: Float,
    val pivotOffset: Offset,
) {
    val action: DrawTransform.() -> Unit = {
        translate(offset.x, offset.y)
        if (rotation != 0f) {
            rotate(rotation)
        }
        scale(scale)
    }

    companion object {
        val IDENTITY = Transform(
            offset = Offset.Zero,
            rotation = 0f,
            scale = 1f,
            pivotOffset = Offset.Zero,
        )
    }
}
