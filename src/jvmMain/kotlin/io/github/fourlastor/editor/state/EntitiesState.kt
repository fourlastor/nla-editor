package io.github.fourlastor.editor.state

import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.Group
import io.github.fourlastor.data.Image
import io.github.fourlastor.data.Transform
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class EntitiesState(
    val root: GroupState,
    val entities: ImmutableList<EntityState>,
) {
    fun asNode(): EntityNode = root.asNode()

    private fun GroupState.asNode() = GroupNode(
        this,
        findChildrenOf(this)
    )

    private fun ImageState.asNode() = ImageNode(
        this,
    )

    private fun findChildrenOf(parent: GroupState): ImmutableList<EntityNode> =
        entities.filter { it.parentId == parent.id }.map {
            when (it) {
                is GroupState -> it.asNode()
                is ImageState -> it.asNode()
            }
        }.toImmutableList()

}

sealed class EntityState(
    val id: Long,
    val parentId: Long?,
    val transform: Transform,
    val name: String,
    val collapsed: Boolean,
)

class GroupState(
    id: Long,
    parentId: Long?,
    transform: Transform,
    name: String,
    collapsed: Boolean,
) : EntityState(id, parentId, transform, name, collapsed)

class ImageState(
    id: Long,
    parentId: Long?,
    name: String = "Image",
    transform: Transform,
    val path: String,
    collapsed: Boolean,
) : EntityState(id, parentId, transform, name, collapsed)

fun Entities.toEntitiesState(): EntitiesState {
    return EntitiesState(
        root = root.groupState(),
        entities = entities.values.map {
            when (it) {
                is Group -> it.groupState()
                is Image -> it.imageState()
            }
        }.toImmutableList()
    )
}

private fun Group.groupState() = GroupState(id, parentId, transform, name, collapsed)
private fun Image.imageState() =
    ImageState(id, parentId, name, transform, path, collapsed)
