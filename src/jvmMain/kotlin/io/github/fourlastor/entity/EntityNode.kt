package io.github.fourlastor.entity

sealed interface EntityNode {
    val entity: Entity
}

data class GroupNode(
    override val entity: Group,
    val children: List<EntityNode>,
) : EntityNode

data class ImageNode(
    override val entity: Image,
) : EntityNode
