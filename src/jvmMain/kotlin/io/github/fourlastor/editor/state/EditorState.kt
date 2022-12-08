package io.github.fourlastor.editor.state

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import io.github.fourlastor.editor.loadImageFromPath
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
        collapsed: Boolean,
        val path: String,
        transform: Transform,
) : EntityState(id, parentId, transform, name, collapsed) {

    init {
        transform.region = Rect(
            left = 0.0f,
            top = 0.0f,
            right = loadImageFromPath(path).width.toFloat(),
            bottom = loadImageFromPath(path).height.toFloat(),
        )
    }
}

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

private fun Group.groupState() = GroupState(id, parentId, transform, name, collapsed)

private fun Image.imageState() =
        ImageState(id, parentId, name, collapsed, path, transform)
