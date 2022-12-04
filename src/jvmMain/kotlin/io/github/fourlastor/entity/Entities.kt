package io.github.fourlastor.entity

data class Entities(
    val entities: List<Entity> = emptyList(),
    val lastId: Long = 0,
) {
    private val root = Group(
        0,
        null,
        emptyList(),
        "Root",
    )

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
                entities = emptyList(),
                transform = transform,
            )
        )
    }

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
                path = path,
                transform = transform,
            ),
        )
    }
}
