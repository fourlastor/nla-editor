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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.fourlastor.entity.Entities
import io.github.fourlastor.entity.Entity
import io.github.fourlastor.entity.EntityNode
import io.github.fourlastor.entity.GroupNode
import io.kanro.compose.jetbrains.expui.control.Icon
import io.kanro.compose.jetbrains.expui.control.Label

/**
 * Property inspector panel.
 * Displays a tree of entities, and when selected it shows the editable data for the selected entity in a form.
 */
@Composable
fun LayersPane(
    entities: Entities,
    modifier: Modifier,
    onEntityChange: (Entity) -> Unit,
    onEntityAdd: (parentId: Long) -> Unit,
) {
    var selectedEntity by remember { mutableStateOf<EntityNode?>(null) }
    val rootNode by remember(entities) { derivedStateOf { entities.asNode() } }

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
    }
}

/** Displays a tree of entities as collapsable elements. */
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

/** Used to make an element selectable. */
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

/**
 *  Displays a group, indenting the children entities before displaying them.
 */
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
                    modifier = Modifier.rotate(if (expanded) 90f else 0f)
                        .clickable(onClick = { expanded = !expanded }),
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

/**
 * Displays a leaf entity, showing a label with its name.
 */
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
