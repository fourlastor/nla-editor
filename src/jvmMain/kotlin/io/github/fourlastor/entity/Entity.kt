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
    fun x(x: Float): Entity
    fun y(y: Float): Entity
    fun rotation(rotation: Float): Entity

    val transform: Transform
    val name: String
}

data class Group(
    val entities: List<Entity>,
    override val name: String = "Group(${entities.size})",
    override val transform: Transform = Transform.IDENTITY,
) : Entity {
    override fun draw(drawScope: DrawScope) = drawScope.withTransform(transform.action) {
        for (entity in entities) {
            entity.draw(this)
        }
    }

    override fun x(x: Float) = copy(transform = transform.x(x))

    override fun y(y: Float) = copy(transform = transform.y(y))

    override fun rotation(rotation: Float) = copy(transform = transform.rotation(rotation))
}

data class Image(
    val image: ImageBitmap,
    override val name: String = "Image",
    override val transform: Transform = Transform.IDENTITY,
) : Entity {
    override fun draw(drawScope: DrawScope) = drawScope.withTransform(transform.action) {
        drawImage(
            image = image,
            dstOffset = intCenter - image.center,
            filterQuality = FilterQuality.None
        )
    }

    override fun x(x: Float) = copy(transform = transform.x(x))

    override fun y(y: Float) = copy(transform = transform.y(y))

    override fun rotation(rotation: Float) = copy(transform = transform.rotation(rotation))
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

    fun x(x: Float) = copy(offset = offset.copy(x = x))

    fun y(y: Float) = copy(offset = offset.copy(y = y))

    fun rotation(rotation: Float) = copy(rotation = rotation)

    companion object {
        val IDENTITY = Transform(
            offset = Offset.Zero,
            rotation = 0f,
            scale = 1f,
            pivotOffset = Offset.Zero,
        )
    }
}
