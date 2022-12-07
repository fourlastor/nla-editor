package io.github.fourlastor.editor.properties

import io.github.fourlastor.data.Transform
import io.github.fourlastor.editor.state.EntitiesState
import io.github.fourlastor.editor.state.EntityState
import io.github.fourlastor.editor.state.GroupState
import io.github.fourlastor.editor.state.ImageState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class PropertiesState(
    val entities: ImmutableList<Entity>
) {
    sealed class Entity(
        val id: Long,
        val name: String,
        val transform: Transform,
    )

    class Group(id: Long, name: String, transform: Transform) : Entity(id, name, transform)
    class Image(id: Long, name: String, transform: Transform) : Entity(id, name, transform)
}

fun EntitiesState.toPropertiesState() = PropertiesState(
    entities = entities.map { it.toEntity() }.toImmutableList()
)

private fun EntityState.toEntity(): PropertiesState.Entity = when (this) {
    is GroupState -> toGroup()
    is ImageState -> toImage()
}

private fun GroupState.toGroup() = PropertiesState.Group(
    id, name, transform
)

private fun ImageState.toImage() = PropertiesState.Image(
    id, name, transform
)
