package io.github.fourlastor.editor.state

import kotlinx.collections.immutable.ImmutableList

sealed class EntityNode {
        abstract val entity: EntityState
}

class GroupNode(
        override val entity: GroupState,
        val children: ImmutableList<EntityNode>,
) : EntityNode()

class ImageNode(
        override val entity: ImageState,
) : EntityNode()
