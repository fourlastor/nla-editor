package io.github.fourlastor.editor.state

import io.github.fourlastor.entity.Entities
import io.github.fourlastor.entity.Group
import io.github.fourlastor.entity.Image
import io.github.fourlastor.entity.Transform
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class EditorState(
        val entities: EntitiesState,
)

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
)

class GroupState(
        id: Long,
        parentId: Long?,
        transform: Transform,
        name: String,
        val collapsed: Boolean,
) : EntityState(id, parentId, transform, name)

class ImageState(
        id: Long,
        parentId: Long?,
        name: String = "Image",
        transform: Transform,
        val path: String,
) : EntityState(id, parentId, transform, name)

fun Entities.toEditorState(): EditorState {
    return EditorState(
            EntitiesState(
                    root = root.groupState(),
                    entities = entities.map {
                        when (it) {
                            is Group -> it.groupState()
                            is Image -> it.imageState()
                        }
                    }.toImmutableList()
            )
    )
}

private fun Group.groupState() = GroupState(id, parentId, transform, name, collapsed = false)

private fun Image.imageState() =
        ImageState(id, parentId, name, transform, path)
