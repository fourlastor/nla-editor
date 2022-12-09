package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.fourlastor.data.Animation
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.data.LatestProject
import io.github.fourlastor.data.LoadableProject
import io.github.fourlastor.editor.save.LoadProject
import io.github.fourlastor.editor.save.SaveProject
import kotlin.time.Duration


@Composable
fun AnimationEditor(
    loadable: LoadableProject,
    entityUpdater: EntityUpdater,
    loadRequested: Boolean,
    saveRequested: Boolean,
    onAddGroup: (parentId: Long) -> Unit,
    onDeleteEntity: (id: Long) -> Unit,
    onAddAnimation: (name: String, duration: Duration) -> Unit,
    onLoadProject: (project: LatestProject) -> Unit,
    onAddImage: (parentId: Long, name: String, path: String) -> Unit,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
    onUpdateAnimation: (animationId: Long, update: (Animation) -> Animation) -> Unit,
    onFinishLoad: () -> Unit,
    onFinishSave: () -> Unit,
) {
    if (loadable !is LoadableProject.Loaded) {
        return
    }
    val project = loadable.result
    val animations = project.animations
    val entities = project.entities

    /** Local state. When this is set, a "new entity" popup is displayed. */
    var newImageParentId: Long? by remember { mutableStateOf(null) }
    EditorUi(
        animations = animations,
        entities = entities,
        entityUpdater = entityUpdater,
        onAddGroup = onAddGroup,
        onDeleteEntity = onDeleteEntity,
        onAddImage = { newImageParentId = it },
        onAddAnimation = onAddAnimation,
        onAddKeyFrame = onAddKeyFrame,
        onUpdateAnimation = onUpdateAnimation,
    )
    if (loadRequested) {
        LoadProject(
            onSuccess = {
                onLoadProject(it)
                onFinishLoad()
            },
            onFailure = {
                println("Failed to load because $it")
                onFinishLoad()
            },
            onCancel = { onFinishLoad() }
        )
    }
    if (saveRequested) {
        SaveProject(
            project = project,
            onSuccess = {
                println("Saved project successfully.")
                onFinishSave()
            },
            onFailure = {
                println("Failed to save because $it")
                onFinishSave()
            },
            onCancel = { onFinishSave() }
        )
    }

    AddImage(newImageParentId, onAddImage = { parentId, name, path ->
        onAddImage(parentId, name, path)
        newImageParentId = null
    }, onCancel = { newImageParentId = null })
}
