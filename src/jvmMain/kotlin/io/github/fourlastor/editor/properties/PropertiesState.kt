package io.github.fourlastor.editor.properties

import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.Entity
import io.github.fourlastor.data.EntityUpdater
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
        val label: String,
    )

    class ReadonlyFloatProperty(
        label: String,
        val value: Float,
    ) : Property(label)

    class FloatProperty(
        label: String,
        val value: Float,
        val updater: PropertyUpdater<Float>
    ) : Property(label)

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
        floatProperty(viewState, "X", transform.x, XUpdater(id)),
        floatProperty(viewState, "Y", transform.y, YUpdater(id)),
        floatProperty(viewState, "Rotation", transform.rotation, RotationUpdater(id)),
    ).toImmutableList()
)

private fun floatProperty(
    viewState: ViewState,
    label: String,
    value: Float,
    updater: PropertiesState.PropertyUpdater<Float>
): PropertiesState.Property {
    return if (viewState.animations is ViewState.Disabled) {
        editorProperty(label, value, updater)
    } else {
        animationProperty(label, value)
    }
}

/** Property affecting directly the entity. */
private fun editorProperty(
    label: String,
    value: Float,
    updater: PropertiesState.PropertyUpdater<Float>
) = PropertiesState.FloatProperty(
    label,
    value,
    updater
)

/** Property affecting animation keys, read only until a keyframe is added at the position. */
private fun animationProperty(
    label: String,
    value: Float
) = PropertiesState.ReadonlyFloatProperty(label, value)

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
