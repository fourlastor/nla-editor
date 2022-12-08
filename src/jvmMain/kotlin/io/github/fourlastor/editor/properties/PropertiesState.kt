package io.github.fourlastor.editor.properties

import io.github.fourlastor.data.Animations
import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.Entity
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.data.Image
import io.github.fourlastor.data.PropertyValue
import io.github.fourlastor.editor.state.ViewState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration

data class PropertiesState(
    val entities: ImmutableList<Entity>
) {
    class Entity(
        val id: Long,
        val name: String,
        val properties: ImmutableList<Property>
    )

    sealed class Property(
        val id: Long,
        val label: String,
    )

    class AnimatedFloatProperty(
        id: Long,
        label: String,
        val value: Float,
        val animationId: Long,
        val entityId: Long,
        val trackPosition: Duration,
    ) : Property(id, label)

    class ReadonlyFloatProperty(
        id: Long,
        label: String,
        val value: Float,
    ) : Property(id, label)

    class FloatProperty(
        id: Long,
        label: String,
        val value: Float,
        val updater: PropertyUpdater<Float>
    ) : Property(id, label)

    sealed class PropertyUpdater<T>(
        val id: Long,
    ) {
        abstract fun update(value: T, entityUpdater: EntityUpdater)
    }
}

fun toPropertiesState(entities: Entities, animations: Animations, viewState: ViewState) = PropertiesState(
    entities = entities.entities.values
        .map { it.toEntity(viewState, animations) }
        .sortedBy { it.id }
        .toImmutableList()
)

private fun Entity.toEntity(viewState: ViewState, animations: Animations): PropertiesState.Entity {

    val imageProperties = if (this is Image) listOf(
        floatProperty(viewState, "Rows", frame.rowsProperty, RowUpdater(id), animations),
        floatProperty(viewState, "Columns", frame.columnsProperty, ColumnUpdater(id), animations),
        floatProperty(viewState, "Frame", frame.frameNumberProperty, FrameNumberUpdater(id), animations),
    ) else emptyList()

    return PropertiesState.Entity(
        id = id,
        name = name,
        properties = (listOf(
            floatProperty(viewState, "X", transform.xProperty, XUpdater(id), animations),
            floatProperty(viewState, "Y", transform.yProperty, YUpdater(id), animations),
            floatProperty(viewState, "Rotation", transform.rotationProperty, RotationUpdater(id), animations),
            floatProperty(viewState, "Scale", transform.scaleProperty, ScaleUpdater(id), animations),
        ) + imageProperties).toImmutableList()
    )
}

private fun Entity.floatProperty(
    viewState: ViewState,
    label: String,
    value: PropertyValue,
    updater: PropertiesState.PropertyUpdater<Float>,
    animations: Animations
): PropertiesState.Property {
    return if (viewState.animations is ViewState.Enabled) {
        animationProperty(label, value, viewState.animations, animations)
    } else {
        editorProperty(label, value, updater)
    }
}

/** Property affecting directly the entity. */
private fun editorProperty(
    label: String,
    value: PropertyValue,
    updater: PropertiesState.PropertyUpdater<Float>
) = PropertiesState.FloatProperty(
    id = value.id,
    label = label,
    value = value.value,
    updater = updater,
)

/** Property affecting animation keys, read only until a keyframe is added at the position. */
private fun Entity.animationProperty(
    label: String,
    value: PropertyValue,
    viewState: ViewState.Enabled,
    animations: Animations
) = if (viewState is ViewState.Selected) {
    PropertiesState.AnimatedFloatProperty(
        id = value.id,
        label = label,
        value = value.value,
        animationId = viewState.id,
        entityId = id,
        trackPosition = viewState.trackPosition
    )
} else {
    PropertiesState.ReadonlyFloatProperty(
        id = value.id,
        label = label,
        value = value.value,
    )
}

private class XUpdater(id: Long) : PropertiesState.PropertyUpdater<Float>(id) {
    override fun update(value: Float, entityUpdater: EntityUpdater) {
        entityUpdater(id) { it.x(value) }
    }
}

private class YUpdater(id: Long) : PropertiesState.PropertyUpdater<Float>(id) {
    override fun update(value: Float, entityUpdater: EntityUpdater) {
        entityUpdater(id) { it.y(value) }
    }
}

private class RotationUpdater(id: Long) : PropertiesState.PropertyUpdater<Float>(id) {
    override fun update(value: Float, entityUpdater: EntityUpdater) {
        entityUpdater(id) { it.rotation(value) }
    }
}

private class ScaleUpdater(id: Long) : PropertiesState.PropertyUpdater<Float>(id) {
    override fun update(value: Float, entityUpdater: EntityUpdater) {
        entityUpdater(id) { it.scale(value) }
    }
}

private class RowUpdater(id: Long) : PropertiesState.PropertyUpdater<Float>(id) {
    override fun update(value: Float, entityUpdater: EntityUpdater) {
        entityUpdater(id) {
            if (it is Image) it.rows(value.toInt())
            else it
        }
    }
}

private class ColumnUpdater(id: Long) : PropertiesState.PropertyUpdater<Float>(id) {
    override fun update(value: Float, entityUpdater: EntityUpdater) {
        entityUpdater(id) {
            if (it is Image) it.columns(value.toInt())
            else it
        }
    }
}

private class FrameNumberUpdater(id: Long) : PropertiesState.PropertyUpdater<Float>(id) {
    override fun update(value: Float, entityUpdater: EntityUpdater) {
        entityUpdater(id) {
            if (it is Image) it.frameNumber(value.toInt())
            else it
        }
    }
}
