package io.github.fourlastor.data

import io.github.fourlastor.system.serializer.DurationAsLong
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class Animations(
    val animations: Map<Long, Animation>,
) {
    operator fun get(id: Long) = checkNotNull(animations[id]) { "Animation with id $id not found" }

    fun animation(animation: Animation): Animations = copy(
        animations = animations.plus(animation.id to animation),
    )

    fun keyFrame(animationId: Long, entityId: Long, propertyId: Long, position: Duration, value: Float): Animations {
        return animation(
            this[animationId].keyFrame(
                entityId,
                propertyId,
                position,
                value
            )
        )
    }

    companion object {
        fun empty(): Animations = Animations(
            animations = emptyMap(),
        )
    }
}

@Serializable
data class Animation(
    val id: Long,
    val name: String,
    val duration: DurationAsLong,
    val tracks: Map<Long, EntityTracks>
) {
    fun keyFrame(entityId: Long, propertyId: Long, position: Duration, value: Float): Animation {
        val entityTracks = tracks.getOrElse(entityId) { EntityTracks(emptyMap()) }
        return copy(
            tracks = tracks.plus(
                entityId to entityTracks.keyFrame(
                    propertyId,
                    position,
                    value
                )
            )
        )
    }
}

@Serializable
data class EntityTracks(
    val properties: Map<Long, Track>
) {
    fun keyFrame(propertyId: Long, position: Duration, value: Float): EntityTracks {
        val track = properties.getOrElse(propertyId) { Track(emptyMap()) }
        return copy(properties = properties.plus(propertyId to track.keyFrame(position, value)))
    }
}

@Serializable
data class Track(
    val keyframes: Map<DurationAsLong, Float>
) {
    fun keyFrame(position: Duration, value: Float): Track {
        return copy(keyframes = keyframes.plus(position to value))
    }
}
