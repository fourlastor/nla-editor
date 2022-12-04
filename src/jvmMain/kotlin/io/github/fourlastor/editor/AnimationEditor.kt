package io.github.fourlastor.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.compose.ui.zIndex
import io.github.fourlastor.entity.*
import io.github.fourlastor.system.FileLoadDialog
import io.github.fourlastor.system.FileSaveDialog
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors
import io.kanro.compose.jetbrains.expui.style.areaBackground
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import io.kanro.compose.jetbrains.expui.window.JBWindow
import kotlinx.serialization.json.Json
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
    var newParentId: Long? by remember { mutableStateOf(null) }
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
            newParentId = newParentId,
            onParentIdChange = { newParentId = it },
            onEntitiesChange = { state = state.copy(entities = it) }
        )
    }
    if (loadRequested) {
        FileLoadDialog {
            if (it != null) {
                // load
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

}

/**
 *  Main UI for the editor, contains the [PreviewPane], [Timeline], and [PropertiesPane].
 *  [entities] entities to display in the editor
 *  [newParentId] state to display (or not) the "create new entity"
 *  [onParentIdChange] callback used to request the creation of a new entity, by setting which group to parent it to
 *  [onEntitiesChange] callback to update [entities]
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun EditorUi(
    entities: Entities,
    newParentId: Long?,
    onParentIdChange: (parentId: Long?) -> Unit,
    onEntitiesChange: (Entities) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PreviewPane(
                entities = entities,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
            )
            Spacer(Modifier.background(LocalAreaColors.current.startBorderColor).height(1.dp).fillMaxWidth())
            Timeline(
                modifier = Modifier
                    .fillMaxSize()
                    .areaBackground()
                    .zIndex(2f),
            )
        }
        Spacer(Modifier.background(LocalAreaColors.current.startBorderColor).width(1.dp).fillMaxHeight())
        PropertiesPane(
            entities = entities,
            modifier = Modifier
                .fillMaxSize()
                .areaBackground()
                .zIndex(2f),
            onEntityChange = {
                onEntitiesChange(entities.update(it))
                onParentIdChange(null)
            },
            onEntityAdd = onParentIdChange,
        )
    }
    newParentId?.also {
        Dialog(
            onCloseRequest = { onParentIdChange(null) },
            state = rememberDialogState(position = WindowPosition(Alignment.Center))
        ) {
            NewEntity(
                parentId = it,
                onAddGroup = { name, parentId ->
                    onEntitiesChange(entities.group(parentId, name))
                    onParentIdChange(null)
                },
                onAddImage = { name, path, parentId ->
                    onEntitiesChange(entities.image(parentId, name, path))
                    onParentIdChange(null)
                },
                onCancel = { onParentIdChange(null) }
            )
        }
    }
}

@Composable
private fun rememberEditorState() = remember {
    mutableStateOf(
        EditorState(
            entities = Entities.empty(),
        )
    )
}

private data class EditorState(
    val entities: Entities,
)
