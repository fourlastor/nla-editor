package io.github.fourlastor.editor

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fourlastor.system.FileDialog
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.TextField
import io.kanro.compose.jetbrains.expui.style.areaBackground

@Composable
fun NewEntity(
    parentId: Long,
    onAddGroup: (name: String, parentId: Long) -> Unit,
    onAddImage: (name: String, path: String, parentId: Long) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var state: State by remember { mutableStateOf(ChoosingType) }
    when (state) {
        is ChoosingType -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .areaBackground()
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ActionButton(onClick = { state = ChosenType.GROUP }) {
                        Label("Group")
                    }
                    ActionButton(onClick = { state = ChosenType.IMAGE }) {
                        Label("Image")
                    }
                }
            }
        }

        ChosenType.GROUP -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .areaBackground()
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    var groupName by remember { mutableStateOf("") }
                    TextField(
                        value = groupName,
                        onValueChange = {
                            groupName = it
                        }
                    )
                    ActionButton(
                        onClick = { onAddGroup(groupName, parentId) },
                        enabled = groupName.trim().isNotEmpty()
                    ) {
                        Label("Create")
                    }
                }
            }
        }

        ChosenType.IMAGE -> {
            FileDialog(onCloseRequest = {
                if (it == null) {
                    onCancel()
                    return@FileDialog
                }

                onAddImage("Image", it.absolutePath, parentId)
            })
        }
    }
}

private sealed interface State
private object ChoosingType : State
private enum class ChosenType : State {
    GROUP,
    IMAGE
}
