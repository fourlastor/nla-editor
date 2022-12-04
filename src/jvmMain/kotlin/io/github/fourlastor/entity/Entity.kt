package io.github.fourlastor.entity

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.scale

sealed interface Entity {
    fun x(x: Float): Entity
    fun y(y: Float): Entity
    fun rotation(rotation: Float): Entity

    val id: Long
    val parentId: Long?
    val transform: Transform
    val name: String
}

data class Group(
    override val id: Long,
    override val parentId: Long?,
    override val name: String = "Group",
    override val transform: Transform = Transform.IDENTITY,
) : Entity {

    override fun x(x: Float) = copy(transform = transform.x(x))

    override fun y(y: Float) = copy(transform = transform.y(y))

    override fun rotation(rotation: Float) = copy(transform = transform.rotation(rotation))
}

data class Image(
    override val id: Long,
    override val parentId: Long?,
    override val name: String = "Image",
    override val transform: Transform = Transform.IDENTITY,
    val path: String,
) : Entity {

    override fun x(x: Float) = copy(transform = transform.x(x))

    override fun y(y: Float) = copy(transform = transform.y(y))

    override fun rotation(rotation: Float) = copy(transform = transform.rotation(rotation))
}

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
