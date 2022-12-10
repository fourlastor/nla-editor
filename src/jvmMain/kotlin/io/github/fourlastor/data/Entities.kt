package io.github.fourlastor.data

import kotlinx.serialization.Serializable

/**
 * A database of [Entity] objects.
 */
@Serializable
data class Entities(
    val entities: Map<Long, Entity>,
) {

    /** Remove an image or group. */
    fun remove(
        id: Long,
    ): Entities {
        val filter = idsForParent(id).toSet()
        return copy(entities = entities.filterKeys { it !in filter })
    }

    private fun idsForParent(parentId: Long): Sequence<Long> = entities.entries.asSequence()
        .filter { (_, entity) -> entity.parentId == parentId }
        .flatMap { (id) -> idsForParent(id) }
        .plusElement(parentId)

    fun entity(entity: Entity): Entities = copy(entities = entities.plus(entity.id to entity))

    operator fun get(id: Long): Entity {
        return checkNotNull(entities[id]) { "Entity with id $id not found" }
    }

    fun children(parentId: Long): List<Entity> =
        entities.values.filter { it.parentId == parentId }

    companion object {

        /** Initial empty state. */
        fun empty() = Entities(
            entities = emptyMap(),
        )
    }
}
