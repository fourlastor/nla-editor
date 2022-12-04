package io.github.fourlastor.entity

import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
sealed interface Entity {
    fun x(x: Float): Entity
    fun y(y: Float): Entity
    fun rotation(rotation: Float): Entity

    val id: Long
    val parentId: Long?
    val transform: Transform
    val name: String
}

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
}

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
}

@Serializable
data class Transform(
    @Serializable(with = OffsetSerializer::class)
    val offset: Offset,
    val rotation: Float,
    val scale: Float,
    @Serializable(with = OffsetSerializer::class)
    val pivotOffset: Offset,
) {
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

@Serializable
@SerialName("Offset")
private data class OffsetSurrogate(
    val x: Float,
    val y: Float,
)
