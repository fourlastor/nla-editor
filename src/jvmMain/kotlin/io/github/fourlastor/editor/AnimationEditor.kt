package io.github.fourlastor.editor

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.rememberWindowState
import io.github.fourlastor.entity.Entities
import io.github.fourlastor.entity.Transform
import io.github.fourlastor.system.FileLoadDialog
import io.github.fourlastor.system.FileSaveDialog
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import io.kanro.compose.jetbrains.expui.window.JBWindow
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

@Composable
        /**
         * Main view, this displays a new window and holds the application state.
         */
fun ApplicationScope.AnimationEditor() {
    /** `state` is the actual editor state, it contains a copy of [Entities]. */
    var state by rememberEditorState()

    /** Local state, it's used to display or not the save popup. */
    var saveRequested by remember { mutableStateOf(false) }

    /** Local state, it's used to display or not the load popup. */
    var loadRequested by remember { mutableStateOf(false) }

    /** Local state. When this is set, a "new entity" popup is displayed. */
    var newImageParentId: Long? by remember { mutableStateOf(null) }

    fun updateEntities(entities: Entities) {
        state = state.copy(entities = entities)
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
            entities = state.entities,
            onEntitiesChange = { updateEntities(it) },
            onAddGroup = { updateEntities(state.entities.group(it, "Group")) },
            onAddImage = { newImageParentId = it },
        )
    }
    if (loadRequested) {
        FileLoadDialog {
            if (it != null) {
                val entities = Json.decodeFromString(
                    Entities.serializer(),
                    it.readText()
                )
                println(entities)
            }
            loadRequested = false
        }
    }
    if (saveRequested) {
        FileSaveDialog {
            if (it != null) {
                val json = Json.encodeToString(Entities.serializer(), state.entities)
                println(json)
            }

            saveRequested = false
        }
    }

    AddImage(
        newImageParentId,
        onAddImage = { parentId, name, path ->
            updateEntities(state.entities.image(parentId, name, path))
            newImageParentId = null
        },
        onCancel = { newImageParentId = null }
    )

}

@Composable
private fun rememberEditorState() = remember {
    val imgPath = File("src/jvmMain/resources/player.png").absolutePath
    mutableStateOf(
        EditorState(
            entities = Entities.empty()
                .image(0, "Hero", imgPath)
                .image(0, "MiniHEro", imgPath, Transform.IDENTITY.copy(scale = 0.4f, rotation = 90f)),
        )
    )
}

private data class EditorState(
    val entities: Entities,
)
