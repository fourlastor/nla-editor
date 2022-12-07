package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.rememberWindowState
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.data.LatestProject
import io.github.fourlastor.data.LoadableProject
import io.github.fourlastor.data.ViewModel
import io.github.fourlastor.editor.save.LoadProject
import io.github.fourlastor.editor.save.SaveProject
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import io.kanro.compose.jetbrains.expui.window.JBWindow
import kotlin.system.exitProcess
import kotlin.time.Duration

@Composable
        /**
         * Main view, this displays a new window and holds the application state.
         */
fun ApplicationScope.AnimationEditor() {
    val viewModel = remember { ViewModel() }
    val project by viewModel.project.collectAsState(LoadableProject.Loading)

    ProjectLoader(
        loadable = project,
        entityUpdater = { id, update -> viewModel.updateEntity(id, update) },
        onAddGroup = { viewModel.group(it, "Group") },
        onDeleteEntity = { viewModel.deleteEntity(it) },
        onCreateAnimation = { name, duration -> viewModel.animation(name, duration) },
        onLoadProject = { viewModel.load(it) },
        onAddImage = { parentId: Long, name: String, path: String -> viewModel.image(parentId, name, path) }
    )
}

@Composable
private fun ApplicationScope.ProjectLoader(
    loadable: LoadableProject,
    entityUpdater: EntityUpdater,
    onAddGroup: (parentId: Long) -> Unit,
    onDeleteEntity: (id: Long) -> Unit,
    onCreateAnimation: (name: String, duration: Duration) -> Unit,
    onLoadProject: (project: LatestProject) -> Unit,
    onAddImage: (parentId: Long, name: String, path: String) -> Unit,
) {
    if (loadable !is LoadableProject.Loaded) {
        return
    }
    val project = loadable.result
    val animations = project.animations
    val entities = project.entities

    /** Local state, it's used to display or not the save popup. */
    var saveRequested by remember { mutableStateOf(false) }

    /** Local state, it's used to display or not the load popup. */
    var loadRequested by remember { mutableStateOf(false) }

    /** Local state. When this is set, a "new entity" popup is displayed. */
    var newImageParentId: Long? by remember { mutableStateOf(null) }

    JBWindow(
        title = "NLA Editor",
        theme = DarkTheme,
        state = rememberWindowState(size = DpSize(900.dp, 700.dp)),
        onCloseRequest = {
            exitApplication()
            exitProcess(0)
        },
        mainToolBar = {
            EditorToolbar(
                onLoad = { loadRequested = true },
                onSave = { saveRequested = true }
            )
        }
    ) {
        EditorUi(
            animations = animations,
            entities = entities,
            entityUpdater = entityUpdater,
            onAddGroup = onAddGroup,
            onDeleteEntity = onDeleteEntity,
            onAddImage = { newImageParentId = it },
            onCreateAnimation = onCreateAnimation,
        )
    }
    if (loadRequested) {
        LoadProject(
            onSuccess = {
                onLoadProject(it)
                loadRequested = false
            },
            onFailure = {
                println("Failed to load because $it")
                loadRequested = false
            },
            onCancel = { loadRequested = false }
        )
    }
    if (saveRequested) {
        SaveProject(
            project = project,
            onSuccess = {
                println("Saved project successfully.")
            },
            onFailure = {
                println("Failed to save because $it")
            },
            onCancel = { saveRequested = false }
        )
    }

    AddImage(
        newImageParentId,
        onAddImage = { parentId, name, path ->
            onAddImage(parentId, name, path)
            newImageParentId = null
        },
        onCancel = { newImageParentId = null }
    )
}
