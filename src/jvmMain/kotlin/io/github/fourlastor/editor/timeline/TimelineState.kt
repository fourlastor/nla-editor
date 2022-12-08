package io.github.fourlastor.editor.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import io.github.fourlastor.data.Animation
import io.github.fourlastor.data.Animations
import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.Entity
import io.github.fourlastor.system.serializer.DurationAsLong
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class TimelineState(
    val position: Duration,
    val duration: Duration,
    val elements: ImmutableList<TimelineElement>,
)

sealed class TimelineElement(
    val key: String,
)

data class Spacer(val id: Long) : TimelineElement("spacer/$id")

data class Track(
    val propertyId: Long,
    val keyFrames: ImmutableList<Pair<Duration, Float>>
) : TimelineElement("track/$propertyId")

@Composable
fun rememberTimelineState(
    entities: Entities,
    animations: Animations,
    animationId: Long,
) = remember(entities, animations, animationId) {
    derivedStateOf {
        val animation = animations[animationId]
        TimelineState(
            position = 0.seconds,
            duration = animation.duration,
            elements = entities.entities.values.flatMap {
                it.toTimelineElements(animation)
            }
                .toImmutableList()
        )
    }
}

private fun Entity.toTimelineElements(animation: Animation): List<TimelineElement> = listOf(
    Spacer(id),
    Track(
        transform.xProperty.id,
        animation.keyFrames(
            entityId = id,
            propertyId = transform.xProperty.id,
        )
    ),
    Track(
        transform.yProperty.id,
        animation.keyFrames(
            entityId = id,
            propertyId = transform.yProperty.id,
        )
    ),
    Track(
        transform.rotationProperty.id,
        animation.keyFrames(
            entityId = id,
            propertyId = transform.rotationProperty.id,
        )
    ),
    Track(
        transform.scaleProperty.id,
        animation.keyFrames(
            entityId = id,
            propertyId = transform.scaleProperty.id,
        )
    )
)

fun Animation.keyFrames(entityId: Long, propertyId: Long): ImmutableList<Pair<DurationAsLong, Float>> = track(entityId)
    .property(propertyId)
    .keyframes.entries.map { (position, value) ->
        position to value
    }
    .toImmutableList()
