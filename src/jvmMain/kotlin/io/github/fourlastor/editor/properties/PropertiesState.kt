package io.github.fourlastor.editor.properties

import io.github.fourlastor.data.Entity
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.data.LatestProject
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

fun LatestProject.toPropertiesState() = PropertiesState(
    entities = entities.entities.values.map { it.toEntity() }.toImmutableList()
)

private fun Entity.toEntity(): PropertiesState.Entity = PropertiesState.Entity(
    id = id,
    name = name,
    properties = listOf<PropertiesState.Property>(
        PropertiesState.FloatProperty(
            "X",
            transform.x,
            XUpdater(id)
        ),
        PropertiesState.FloatProperty(
            "Y",
            transform.y,
            YUpdater(id)
        ),
        PropertiesState.FloatProperty(
            "Rotation",
            transform.rotation,
            RotationUpdater(id)
        ),
    ).toImmutableList()
)

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
