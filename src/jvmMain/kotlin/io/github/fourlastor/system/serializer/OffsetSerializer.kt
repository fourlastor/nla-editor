package io.github.fourlastor.system.serializer

import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** JSON serializer for [Offset]. Delegates to a surrogate class. */
object OffsetSerializer : KSerializer<Offset> {
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
