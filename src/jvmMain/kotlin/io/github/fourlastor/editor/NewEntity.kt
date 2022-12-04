package io.github.fourlastor.editor

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fourlastor.system.FileLoadDialog
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.TextField
import io.kanro.compose.jetbrains.expui.style.areaBackground

/**
 * Popup to create a new entity.
 * [parentId] id of the parent group to be attached to.
 * [onAddGroup] callback to create a new group
 * [onAddImage] callback to create a new image
 * [onCancel] callback to cancel the operation
 */
@Composable
fun NewEntity(
    parentId: Long,
    onAddGroup: (name: String, parentId: Long) -> Unit,
    onAddImage: (name: String, path: String, parentId: Long) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    /**
     * "Steps" in adding a new entity
     * [ChoosingType] the user is choosing the type of entity (pick a type)
     * [ChosenType.GROUP] the user wants to add a group (create a new group)
     * [ChosenType.IMAGE] the user wants to add an image (create a new image)
     */
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
            FileLoadDialog(onCloseRequest = {
                if (it == null) {
                    onCancel()
                    return@FileLoadDialog
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
