package io.github.fourlastor.editor.properties

import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.Entity
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.data.PropertyValue
import io.github.fourlastor.editor.state.ViewState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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

fun toPropertiesState(entities: Entities, viewState: ViewState) = PropertiesState(
    entities = entities.entities.values
        .map { it.toEntity(viewState) }
        .sortedBy { it.id }
        .toImmutableList()
)

private fun Entity.toEntity(viewState: ViewState): PropertiesState.Entity = PropertiesState.Entity(
    id = id,
    name = name,
    properties = listOf(
        floatProperty(viewState, "X", transform.xProperty, XUpdater(id)),
        floatProperty(viewState, "Y", transform.yProperty, YUpdater(id)),
        floatProperty(viewState, "Rotation", transform.rotationProperty, RotationUpdater(id)),
        floatProperty(viewState, "Scale", transform.scaleProperty, ScaleUpdater(id)),
    ).toImmutableList()
)

private fun Entity.floatProperty(
    viewState: ViewState,
    label: String,
    value: PropertyValue,
    updater: PropertiesState.PropertyUpdater<Float>
): PropertiesState.Property {
    return if (viewState.animations is ViewState.Enabled) {
        animationProperty(label, value, viewState.animations)
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
    viewState: ViewState.Enabled
) = if (viewState is ViewState.Selected) {
    PropertiesState.AnimatedFloatProperty(
        id = value.id,
        label = label,
        value = value.value,
        animationId = viewState.id,
        entityId = id,
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
