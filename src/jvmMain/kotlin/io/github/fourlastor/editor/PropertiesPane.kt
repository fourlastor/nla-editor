package io.github.fourlastor.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.entity.*
import io.kanro.compose.jetbrains.expui.control.Icon
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.TextField

/**
 * Property inspector panel.
 * Displays a tree of entities, and when selected it shows the editable data for the selected entity in a form.
 */
@Composable
fun PropertiesPane(
    entities: Entities,
    modifier: Modifier,
    onEntityChange: (Entity) -> Unit,
    onEntityAdd: (parentId: Long) -> Unit,
) {
    var selectedEntity by remember { mutableStateOf<EntityNode?>(null) }
    val rootNode by remember(entities) { derivedStateOf { entities.asNode() } }

    val selected = selectedEntity
    if (selected != null) {
        PropertyEditor(
            node = selected,
            onEntityChange = onEntityChange,
            onEntityAdd = onEntityAdd,
        )
    }
}

/** Displays properties of an entity in a form. */
@Composable
private fun PropertyEditor(node: EntityNode, onEntityChange: (Entity) -> Unit, onEntityAdd: (parentId: Long) -> Unit) {
    Column {
        val entity = node.entity
        Label(entity.name, fontSize = 12.sp)
        TransformEditor(
            transform = entity.transform,
            onXChange = { onEntityChange(entity.x(it)) },
            onYChange = { onEntityChange(entity.y(it)) },
            onRotationChange = { onEntityChange(entity.rotation(it)) }
        )
        when (node) {
            is GroupNode -> GroupEditor(node, onEntityAdd)
            is ImageNode -> ImageEditor(node)
        }
    }
}

/** Displays a form to edit an entity [Transform]. */
@Composable
private fun TransformEditor(
    transform: Transform,
    onXChange: (Float) -> Unit,
    onYChange: (Float) -> Unit,
    onRotationChange: (Float) -> Unit,
) {
    Column {
        NumberField(transform.translation.x, onXChange, "X")
        NumberField(transform.translation.y, onYChange, "Y")
        NumberField(transform.rotation, onRotationChange, "Rotation")
    }
}

@Composable
private fun NumberField(value: Float, onValueChange: (Float) -> Unit, label: String) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Label(text = label)
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.onFocusChanged {
                if (!it.hasFocus) {
                    val newValue = text.toFloatOrNull() ?: 0f
                    text = newValue.toString()
                    onValueChange(newValue)
                }
            },
        )
    }
}

/** Adds a + button when editing a group, to add a new entity to it. */
@Composable
private fun GroupEditor(group: GroupNode, onEntityAdd: (parentId: Long) -> Unit) = group.run {
    Column(modifier = Modifier.padding(start = 12.dp)) {
        Icon(
            resource = "icons/add.svg",
            modifier = Modifier
                .clickable(onClick = { onEntityAdd(group.entity.id) }),
        )
    }
}

@Composable
private fun ImageEditor(
    @Suppress("UNUSED_PARAMETER") image: EntityNode, // one day
) {
    Label("TODO")
}
