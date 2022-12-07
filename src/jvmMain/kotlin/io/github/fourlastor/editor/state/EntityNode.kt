package io.github.fourlastor.editor.state

import kotlinx.collections.immutable.ImmutableList

sealed class EntityNode

class GroupNode(
        val entity: GroupState,
        val children: ImmutableList<EntityNode>,
) : EntityNode()

class ImageNode(
        val entity: ImageState,
) : EntityNode()
