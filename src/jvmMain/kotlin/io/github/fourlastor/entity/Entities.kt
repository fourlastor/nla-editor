package io.github.fourlastor.entity

import kotlinx.serialization.Serializable

/**
 * A database of [Entity] objects.
 * Keeps track of the last used id in [lastId].
 */
@Serializable
data class Entities(
    val entities: List<Entity>,
    val lastId: Long,
    val root: Group,
) {

    /** Updates the entity [entity] in the collection, matching by [Entity.id]. */
    fun update(entity: Entity): Entities = copy(
        entities = entities.map { if (it.id == entity.id) entity else it },
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
            entities = entities + Group(
                id = newId,
                parentId = parent,
                name = name,
                transform = transform,
                collapsed = false,
                frame = Frame(1, 1, 0),
            )
        )
    }

    /** Remove an image or group. */
    fun remove(
        id: Long,
    ): Entities {
        return copy(entities = entities.filter { it.id != id })
    }

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
                entities = entities + Image(
                        id = newId,
                        parentId = parent,
                        name = name,
                        transform = transform,
                        path = path,
                        collapsed = false,
                        frame = Frame(1, 1, 0),
                ),
        )
    }

    fun byId(id: Long): Entity {
        if (id == 0L) return root
        return entities.first { it.id == id }
    }

    companion object {

        /** Initial empty state. */
        fun empty() = Entities(
                entities = emptyList(),
                lastId = 0,
                root = Group(
                        0,
                        null,
                        "Root",
                        Transform.IDENTITY,
                        collapsed = false,
                        frame = Frame(1, 1, 0),
                )
        )
    }
}
