package io.github.fourlastor.entity

import kotlinx.serialization.Serializable

/**
 * A database of [Entity] objects.
 * Can produce a tree-structure of them via [asNode].
 * Keeps track of the last used id in [lastId].
 */
@Serializable
data class Entities(
    val entities: List<Entity>,
    val lastId: Long,
    val root: Group,
) {
    fun asNode(): EntityNode = root.asNode()

    private fun Group.asNode() = GroupNode(
        this,
        findChildrenOf(this)
    )

    private fun Image.asNode() = ImageNode(
        this,
    )

    private fun findChildrenOf(parent: Group): List<EntityNode> =
        entities.filter { it.parentId == parent.id }.map {
            when (it) {
                is Group -> it.asNode()
                is Image -> it.asNode()
            }
        }

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
            )
        )
    }

    /** Remove an image or group. */
    fun remove(
        entity: Entity
    ): Entities {
        return copy(entities = entities.drop(entities.indexOf(entity)))
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
            ),
        )
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
            )
        )
    }
}
