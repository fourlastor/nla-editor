package io.github.fourlastor.data

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
    fun scale(scale: Float): Entity
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
    override fun scale(scale: Float) = copy(transform = transform.scale(scale))
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

    override fun scale(scale: Float) = copy(transform = transform.scale(scale))

    override fun name(name: String) = copy(name = name)

    override fun collapsed(collapsed: Boolean) = copy(collapsed = collapsed)
}

/**
 * Represents the transform ([x], [y], [rotation], and [scale]) of an [Entity].
 */
@Serializable
data class Transform(
    val xProperty: PropertyValue,
    val yProperty: PropertyValue,
    val rotationProperty: PropertyValue,
    val scaleProperty: PropertyValue,
) {
    val x: Float
        get() = xProperty.value
    val y: Float
        get() = yProperty.value

    val rotation: Float
        get() = rotationProperty.value

    val scale: Float
        get() = scaleProperty.value

    fun x(x: Float) = copy(xProperty = xProperty.copy(value = x))

    fun y(y: Float) = copy(yProperty = yProperty.copy(value = y))

    fun rotation(rotation: Float) = copy(rotationProperty = rotationProperty.copy(value = rotation))
    fun scale(scale: Float) = copy(scaleProperty = scaleProperty.copy(value = scale))
}
