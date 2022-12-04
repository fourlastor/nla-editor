package io.github.fourlastor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.entity.*
import io.kanro.compose.jetbrains.expui.control.Icon
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.TextField

@Composable
fun PropertiesPane(
    state: EditorState,
    modifier: Modifier,
    onEntityChange: (Entity) -> Unit,
) {
    var selectedEntity by remember { mutableStateOf<EntityNode?>(null) }
    val rootNode by remember { derivedStateOf { state.entities.asNode() } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        EntityTreeView(
            entity = rootNode,
            modifier = Modifier.fillMaxHeight(0.7f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            selectedEntity = selectedEntity,
            onEntitySelected = { it: EntityNode ->
                selectedEntity = it
            },
        )
        val selected = selectedEntity
        if (selected != null) {
            PropertyEditor(selected) {
                // TODO: re-enable editing
            }
        }
    }
}

@Composable
private fun EntityTreeView(
    entity: EntityNode,
    selectedEntity: EntityNode?,
    onEntitySelected: (EntityNode) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (entity is GroupNode) {
        GroupNodeView(entity, selectedEntity, onEntitySelected, modifier)
    } else {
        EntityNodeView(entity, selectedEntity, onEntitySelected, modifier)
    }
}

val selectedColor = Color(0.3f, 0.5f, 0.8f)

@Composable
private fun Selectable(
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val bgColor = if (selected) {
        selectedColor
    } else Color.Unspecified
    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onSelected,
            )
            .background(bgColor),
        content = content,
    )

}

@Composable
private fun GroupNodeView(
    group: GroupNode,
    selectedEntity: EntityNode?,
    onEntitySelected: (EntityNode) -> Unit,
    modifier: Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier,
    ) {
        Selectable(
            modifier = Modifier.fillMaxWidth(),
            selected = group == selectedEntity,
            onSelected = { onEntitySelected(group) }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    resource = "icons/arrow.svg",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (expanded) 90f else 0f)
                        .clickable {
                            expanded = !expanded
                        },
                )
                Label(group.entity.name)
            }
        }
        if (expanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(start = 24.dp).fillMaxWidth(),
            ) {
                group.children.forEach { EntityTreeView(it, selectedEntity, onEntitySelected, Modifier.fillMaxWidth()) }
            }
        }
    }
}

@Composable
private fun EntityNodeView(
    entity: EntityNode,
    selectedEntity: EntityNode?,
    onEntitySelected: (EntityNode) -> Unit,
    modifier: Modifier,
) {
    Selectable(selected = entity == selectedEntity, onSelected = { onEntitySelected(entity) }, modifier = modifier) {
        Label(entity.entity.name)
    }
}

@Composable
private fun PropertyEditor(node: EntityNode, onEntityChange: (Entity) -> Unit) {
    Column {
        val entity = node.entity
        Label(entity.name, fontSize = 12.sp)
        TransformEditor(
            transform = entity.transform,
            onXChange = { onEntityChange(entity.x(it)) },
            onYChange = { onEntityChange(entity.y(it)) },
            onRotationChange = { onEntityChange(entity.rotation(it)) }
        )
//        when (entity) {
//            is Group -> GroupEditor(entity, onEntityChange)
//            is Image -> ImageEditor(entity)
//        }
    }
}

@Composable
private fun TransformEditor(
    transform: Transform,
    onXChange: (Float) -> Unit,
    onYChange: (Float) -> Unit,
    onRotationChange: (Float) -> Unit,
) {
    Column {
        NumberField(transform.offset.x, onXChange, "X")
        NumberField(transform.offset.y, onYChange, "Y")
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

@Composable
private fun GroupEditor(group: GroupNode, onEntityChange: (Entity) -> Unit) = group.run {
    Column(modifier = Modifier.padding(start = 12.dp)) {
        group.children.forEachIndexed { originalIndex, entity ->
            PropertyEditor(
                node = entity
            ) {
//                onEntityChange(group.copy(entities = group.entities.mapIndexed { i, e ->
//                    if (i == originalIndex) {
//                        it
//                    } else {
//                        e
//                    }
//                }))
            }
        }
    }
}

@Composable
private fun ImageEditor(
    @Suppress("UNUSED_PARAMETER") image: Image, // one day
) {
    Label("TODO")
}
