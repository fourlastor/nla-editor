package io.github.fourlastor.data

import androidx.compose.ui.geometry.Offset
import io.github.fourlastor.system.serializer.OffsetSerializer
import kotlinx.serialization.Serializable

/**
 * An animation is composed of elements which have properties (such as position, rotation, scale).
 * [Entity] is the base interface for this class, defining the base fields.
 *
 * [id] unique ID of this entity
 * [parentId] id of the parent [Group] containing this entity, null if it's the root node.
 * [transform] transform of this entity
 * [name] human name for this entity
 */
@Serializable
sealed interface Entity {
    fun x(x: Float): Entity
    fun y(y: Float): Entity
    fun rotation(rotation: Float): Entity
    fun name(name: String): Entity
    fun collapsed(collapsed: Boolean): Entity

    val id: Long
    val parentId: Long?
    val transform: Transform
    val name: String
    val collapsed: Boolean
}

/**
 * A [Group] is an [Entity] which only contains children entities.
 * The children are associated by [parentId].
 */
@Serializable
data class Group(
    override val id: Long,
    override val parentId: Long?,
    override val name: String = "Group",
    override val transform: Transform,
    override val collapsed: Boolean,
) : Entity {

    override fun x(x: Float) = copy(transform = transform.x(x))

    override fun y(y: Float) = copy(transform = transform.y(y))

    override fun rotation(rotation: Float) = copy(transform = transform.rotation(rotation))

    override fun name(name: String) = copy(name = name)
    override fun collapsed(collapsed: Boolean) = copy(collapsed = collapsed)
}

/**
 *  An [Entity] representing an image.
 *  [path] absolute path to the image file.
 */
@Serializable
data class Image(
    override val id: Long,
    override val parentId: Long?,
    override val name: String = "Image",
    override val transform: Transform,
    override val collapsed: Boolean,
    val path: String,
) : Entity {

    override fun x(x: Float) = copy(transform = transform.x(x))

    override fun y(y: Float) = copy(transform = transform.y(y))

    override fun rotation(rotation: Float) = copy(transform = transform.rotation(rotation))

    override fun name(name: String) = copy(name = name)

    override fun collapsed(collapsed: Boolean) = copy(collapsed = collapsed)
}

/**
 * Represents the transform ([translation], [rotation], and [scale]) of an [Entity].
 */
@Serializable
data class Transform(
    @Serializable(with = OffsetSerializer::class)
    val translation: Offset,
    val rotation: Float,
    val scale: Float,
    @Serializable(with = OffsetSerializer::class)
    val pivotOffset: Offset,
) {
    val x: Float
        get() = translation.x
    val y: Float
        get() = translation.y

    fun x(x: Float) = copy(translation = translation.copy(x = x))

    fun y(y: Float) = copy(translation = translation.copy(y = y))

    fun rotation(rotation: Float) = copy(rotation = rotation)

    companion object {
        val IDENTITY = Transform(
            translation = Offset.Zero,
            rotation = 0f,
            scale = 1f,
            pivotOffset = Offset.Zero,
        )
    }
}
