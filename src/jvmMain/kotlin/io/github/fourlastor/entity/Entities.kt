package io.github.fourlastor.entity

import kotlinx.serialization.Serializable

/**
 * A database of [Entity] objects.
 * Keeps track of the last used id in [lastId].
 */
@Serializable
data class Entities(
    val entities: Map<Long, Entity>,
    val lastId: Long,
    val root: Group,
) {

    /** Updates the entity [entity] in the collection, matching by [Entity.id]. */
    fun update(entity: Entity): Entities = copy(
        entities = entities.minus(entity.id).plus(entity.id to entity)
    )

    /** Creates a new group, using the next available id. */
    fun group(
        parent: Long,
        name: String,
        transform: Transform = Transform.IDENTITY,
    ): Entities {
        val newId = lastId + 1
        return copy(
            lastId = newId,
            entities = entities.plus(
                newId to Group(
                    id = newId,
                    parentId = parent,
                    name = name,
                    transform = transform,
                    collapsed = false,
                )
            )
        )
    }

    /** Remove an image or group. */
    fun remove(
        id: Long,
    ): Entities {
        val filter = idsForParent(id).toSet()
        return copy(entities = entities.filter { (id, _) -> id !in filter })
    }

    private fun idsForParent(parentId: Long): Sequence<Long> = entities.entries.asSequence()
        .filter { (_, entity) -> entity.parentId == parentId }
        .flatMap { (id) -> idsForParent(id) }
        .plusElement(parentId)

    /** Creates a new image, using the next available id. */
    fun image(
        parent: Long,
        name: String,
        path: String,
        transform: Transform = Transform.IDENTITY,
    ): Entities {
        val newId = lastId + 1
        return copy(
            lastId = newId,
            entities = entities.plus(
                newId to Image(
                    id = newId,
                    parentId = parent,
                    name = name,
                    transform = transform,
                    path = path,
                        collapsed = false,
                )
            ),
        )
    }

    fun byId(id: Long): Entity {
        if (id == 0L) return root
        return checkNotNull(entities[id]) { "Entity with id $id not found" }
    }

    companion object {

        /** Initial empty state. */
        fun empty() = Entities(
            entities = emptyMap(),
            lastId = 0,
            root = Group(
                0,
                null,
                "Root",
                Transform.IDENTITY,
            collapsed = false,
                )
        )
    }
}
