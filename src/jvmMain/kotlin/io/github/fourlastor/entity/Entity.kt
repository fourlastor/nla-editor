package io.github.fourlastor.entity

import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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

    val id: Long
    val parentId: Long?
    val transform: Transform
    val name: String
}

/**
 * A [Group] is an [Entity] which only contains children entities.
 * The children are associated by [parentId], see [Entities.asNode] for accessing them in a tree-like manner.
 */
@Serializable
data class Group(
    override val id: Long,
    override val parentId: Long?,
    override val name: String = "Group",
    override val transform: Transform,
) : Entity {

    override fun x(x: Float) = copy(transform = transform.x(x))

    override fun y(y: Float) = copy(transform = transform.y(y))

    override fun rotation(rotation: Float) = copy(transform = transform.rotation(rotation))

    override fun name(name: String) = copy(name = name)
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
    val path: String,
) : Entity {

    override fun x(x: Float) = copy(transform = transform.x(x))

    override fun y(y: Float) = copy(transform = transform.y(y))

    override fun rotation(rotation: Float) = copy(transform = transform.rotation(rotation))

    override fun name(name: String) = copy(name = name)
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

/** JSON serializer for [Offset]. Delegates to a surrogate class. */
private object OffsetSerializer : KSerializer<Offset> {
    override val descriptor: SerialDescriptor
        get() = OffsetSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): Offset {
        val surrogate = decoder.decodeSerializableValue(OffsetSurrogate.serializer())
        return Offset(surrogate.x, surrogate.y)
    }

    override fun serialize(encoder: Encoder, value: Offset) {
        val surrogate = OffsetSurrogate(value.x, value.y)
        encoder.encodeSerializableValue(OffsetSurrogate.serializer(), surrogate)
    }

}

/**
 * [Offset] JSON surrogate class.
 * Used to generate a serializer usable with [Offset], which isn't @[Serializable]
 */
@Serializable
@SerialName("Offset")
private data class OffsetSurrogate(
    val x: Float,
    val y: Float,
)
