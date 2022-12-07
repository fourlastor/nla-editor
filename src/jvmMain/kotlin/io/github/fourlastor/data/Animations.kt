package io.github.fourlastor.data

import io.github.fourlastor.system.serializer.DurationAsLong
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class Animations(
    val animations: Map<Long, Animation>,
    val lastId: Long,
) {
    fun byId(id: Long) = checkNotNull(animations[id]) { "Animation with id $id not found" }

    fun animation(name: String, duration: Duration): Animations {
        val id = lastId + 1
        return copy(
            animations = animations.plus(
                id to Animation(
                    id, name, duration, emptyMap()
                )
            ),
            lastId = id,
        )
    }

    companion object {
        fun empty(): Animations = Animations(
            animations = emptyMap(),
            lastId = 0,
        )
    }
}

@Serializable
data class Animation(
    val id: Long,
    val name: String,
    val duration: DurationAsLong,
    val tracks: Map<Long, EntityTracks>
)

@Serializable
data class EntityTracks(
    val entityId: Long,
    val tracks: Map<String, Track>
)

@Serializable
sealed interface Track {
    val path: String
}

@Serializable
data class FloatTrack(
    override val path: String,
    val keyframes: List<Keyframe<Float>>
) : Track

@Serializable
data class Keyframe<T>(
    val position: DurationAsLong,
    val value: T,
)
