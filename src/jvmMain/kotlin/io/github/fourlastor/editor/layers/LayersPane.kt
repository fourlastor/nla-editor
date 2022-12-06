package io.github.fourlastor.editor.layers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.editor.TransparentField
import io.github.fourlastor.editor.icon.MediumIcon
import io.github.fourlastor.editor.icon.SmallIcon
import io.github.fourlastor.entity.*
import io.github.fourlastor.system.Selectable
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.Label

/**
 * Layers panel.
 * Displays a tree of entities, with editable names
 */
@Composable
fun LayersPane(
    entities: Entities,
    modifier: Modifier,
    onEntityChange: (Entity) -> Unit,
    onEntityAdd: (parentId: Long, type: EntityType) -> Unit,
) {
    var selectedEntity by remember { mutableStateOf<EntityNode?>(null) }
    val rootNode by remember(entities) { derivedStateOf { entities.asNode() } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Label(
                text = "Layers",
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            AddEntitiesButtons(selectedEntity, onEntityAdd)
        }
        EntityTreeView(
            entity = rootNode,
            modifier = Modifier.fillMaxHeight(0.7f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            selectedEntity = selectedEntity,
            onEntitySelected = { it: EntityNode ->
                selectedEntity = it
            },
            onEntityChange = onEntityChange,
        )
    }
}

@Composable
private fun AddEntitiesButtons(
    selectedEntity: EntityNode?,
    onEntityAdd: (parentId: Long, type: EntityType) -> Unit,
) {
    if (selectedEntity !is GroupNode) {
        return
    }
    AddButton(
        parentId = selectedEntity.entity.id,
        type = EntityType.GROUP,
        onEntityAdd = onEntityAdd
    )
    AddButton(
        parentId = selectedEntity.entity.id,
        type = EntityType.IMAGE,
        onEntityAdd = onEntityAdd
    )
}

@Composable
private fun AddButton(
    parentId: Long,
    type: EntityType,
    onEntityAdd: (parentId: Long, type: EntityType) -> Unit,
) {
    ActionButton(
        onClick = {
            onEntityAdd(parentId, type)
        },
        modifier = Modifier.padding(4.dp)
    ) {
        MediumIcon(type.icon)
    }
}

/** Displays a tree of entities as collapsable elements. */
@Composable
private fun EntityTreeView(
    entity: EntityNode,
    selectedEntity: EntityNode?,
    onEntitySelected: (EntityNode) -> Unit,
    modifier: Modifier = Modifier,
    onEntityChange: (Entity) -> Unit,
) {
    if (entity is GroupNode) {
        GroupNodeView(entity, selectedEntity, onEntitySelected, modifier, onEntityChange)
    } else {
        EntityNodeView(entity, selectedEntity, onEntitySelected, modifier, onEntityChange)
    }
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
    onEntityChange: (Entity) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Selectable(
            modifier = Modifier.fillMaxWidth(),
            selected = group == selectedEntity,
            onSelected = { onEntitySelected(group) },
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                EntityName("icons/group.svg", group.entity, onEntityChange)
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(start = 16.dp).fillMaxWidth(),
        ) {
            group.children.forEach {
                EntityTreeView(
                    it,
                    selectedEntity,
                    onEntitySelected,
                    Modifier.fillMaxWidth(),
                    onEntityChange
                )
            }
        }
    }
}

@Composable
private fun EntityName(
    icon: String,
    entity: Entity,
    onEntityChange: (Entity) -> Unit,
) {
    SmallIcon(icon)
    TransparentField(
        value = entity.name,
        validator = { it },
        onValueChange = { onEntityChange(entity.name(it)) },
        textStyle = TextStyle(fontSize = 16.sp)
    )
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
    onEntityChange: (Entity) -> Unit,
) {
    Selectable(
        selected = entity == selectedEntity,
        onSelected = { onEntitySelected(entity) },
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            EntityName(
                icon = "icons/image.svg",
                entity = entity.entity,
                onEntityChange,
            )
        }
    }
}
