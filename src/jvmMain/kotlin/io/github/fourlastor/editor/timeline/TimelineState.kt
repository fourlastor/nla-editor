package io.github.fourlastor.editor.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import io.github.fourlastor.data.Animation
import io.github.fourlastor.data.Animations
import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.Entity
import io.github.fourlastor.data.Image
import io.github.fourlastor.editor.state.ViewState
import io.github.fourlastor.system.serializer.DurationAsLong
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration

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
    animationState: ViewState.Selected,
    entities: Entities,
    animations: Animations,
    animationId: Long,
) = remember(animationState, entities, animations, animationId) {
    derivedStateOf {
        val animation = animations[animationId]
        TimelineState(
            position = animationState.trackPosition,
            duration = animation.duration,
            elements = entities.entities.values.flatMap {
                it.toTimelineElements(animation)
            }
                .toImmutableList()
        )
    }
}

private fun Entity.toTimelineElements(animation: Animation): List<TimelineElement> {
    val trackElements = listOf(
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
        ),
    )
    val imageTrackElements = if (this is Image) listOf(
        Track(
            this.frame.rowsProperty.id,
            animation.keyFrames(
                entityId = id,
                propertyId = this.frame.rowsProperty.id,
            )
        ),
        Track(
            this.frame.columnsProperty.id,
            animation.keyFrames(
                entityId = id,
                propertyId = this.frame.columnsProperty.id,
            )
        ),
        Track(
            this.frame.frameNumberProperty.id,
            animation.keyFrames(
                entityId = id,
                propertyId = this.frame.frameNumberProperty.id,
            )
        ),
    ) else emptyList()

    return trackElements + imageTrackElements
}

fun Animation.keyFrames(entityId: Long, propertyId: Long): ImmutableList<Pair<DurationAsLong, Float>> = track(entityId)
    .property(propertyId)
    .keyframes.entries.map { (position, value) ->
        position to value
    }
    .toImmutableList()
