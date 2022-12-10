package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.fourlastor.data.Animation
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.data.LoadableProject
import kotlin.time.Duration


@Composable
fun AnimationEditor(
    project: LoadableProject,
    entityUpdater: EntityUpdater,
    onAddGroup: (parentId: Long) -> Unit,
    onDeleteEntity: (id: Long) -> Unit,
    onAddAnimation: (name: String, duration: Duration) -> Unit,
    onAddImage: (parentId: Long, name: String, path: String) -> Unit,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
    onUpdateAnimation: (animationId: Long, update: (Animation) -> Animation) -> Unit,
) {
    if (project !is LoadableProject.Loaded) {
        return
    }

    /** Local state. When this is set, a "new entity" popup is displayed. */
    var newImageParentId: Long? by remember { mutableStateOf(null) }
    EditorUi(
        project = project,
        entityUpdater = entityUpdater,
        onAddGroup = onAddGroup,
        onDeleteEntity = onDeleteEntity,
        onAddImage = { newImageParentId = it },
        onAddAnimation = onAddAnimation,
        onAddKeyFrame = onAddKeyFrame,
        onUpdateAnimation = onUpdateAnimation,
    )

    AddImage(
        parentId = newImageParentId,
        projectPath = project.path,
        onAddImage = { parentId, name, path ->
            onAddImage(parentId, name, path)
            newImageParentId = null
        },
        onCancel = { newImageParentId = null }
    )
}
