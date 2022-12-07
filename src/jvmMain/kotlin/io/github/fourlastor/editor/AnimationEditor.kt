package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.rememberWindowState
import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.demoData
import io.github.fourlastor.editor.save.LoadProject
import io.github.fourlastor.editor.save.SaveProject
import io.github.fourlastor.editor.state.EditorState
import io.github.fourlastor.editor.state.toEntitiesState
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import io.kanro.compose.jetbrains.expui.window.JBWindow
import kotlin.system.exitProcess

@Composable
/**
 * Main view, this displays a new window and holds the application state.
 */
fun ApplicationScope.AnimationEditor() {
    /** `state` is the actual editor state, it contains a copy of [Entities]. */
    var project by remember { mutableStateOf(demoData()) }
    val entities = project.entities
    val editorState by remember(entities) {
        derivedStateOf {
            EditorState(
                entities = entities.toEntitiesState(),
            )
        }
    }

    /** Local state, it's used to display or not the save popup. */
    var saveRequested by remember { mutableStateOf(false) }

    /** Local state, it's used to display or not the load popup. */
    var loadRequested by remember { mutableStateOf(false) }

    /** Local state. When this is set, a "new entity" popup is displayed. */
    var newImageParentId: Long? by remember { mutableStateOf(null) }


    fun updateEntities(entities: Entities) {
        project = project.copy(entities = entities)
    }

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
            project = project,
            state = editorState,
            entityUpdater = { id, update ->
                updateEntities(entities = entities.update(update(entities.byId(id))))
            },
            onAddGroup = { updateEntities(entities.group(it, "Group")) },
            onDeleteNode = { updateEntities(entities.remove(it)) },
            onAddImage = { newImageParentId = it },
            onUpdateProject = { project = it }
        )
    }
    if (loadRequested) {
        LoadProject(
            onSuccess = {
                project = it
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
                saveRequested = false
            },
            onFailure = {
                println("Failed to save because $it")
                saveRequested = false
            },
            onCancel = { saveRequested = false }
        )
    }

    AddImage(
        newImageParentId,
        onAddImage = { parentId, name, path ->
            updateEntities(entities.image(parentId, name, path))
            newImageParentId = null
        },
        onCancel = { newImageParentId = null }
    )
}
