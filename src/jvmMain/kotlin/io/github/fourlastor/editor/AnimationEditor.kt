package io.github.fourlastor.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.zIndex
import io.github.fourlastor.entity.*
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors
import io.kanro.compose.jetbrains.expui.style.areaBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimationEditor() {
    var state by rememberEditorState()
    var newParentId: Long? by remember { mutableStateOf(null) }
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
                entities = state.entities,
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
            entities = state.entities,
            modifier = Modifier
                .fillMaxSize()
                .areaBackground()
                .zIndex(2f),
            onEntityChange = {
                val entities = state.entities.update(it)
                state = state.copy(entities = entities)
                newParentId = null
            },
        ) { newParentId = it }
    }
    newParentId?.also {
        Dialog(
            onCloseRequest = { newParentId = null },
            state = rememberDialogState(position = WindowPosition(Alignment.Center))
        ) {
            NewEntity(
                parentId = it,
                onAddGroup = { name, parentId ->
                    state = state.group(parentId, name)
                    newParentId = null
                },
                onAddImage = { name, path, parentId ->
                    state = state.image(parentId, name, path)
                    newParentId = null
                },
                onCancel = { newParentId = null }
            )
        }
    }
}

@Composable
private fun rememberEditorState() = remember {
    mutableStateOf(
        EditorState(
            entities = Entities(),
        )
    )
}

private data class EditorState(
    val entities: Entities,
) {
    fun group(parent: Long, name: String) = copy(entities = entities.group(parent, name))
    fun image(parent: Long, name: String, path: String) = copy(entities = entities.image(parent, name, path))
}
