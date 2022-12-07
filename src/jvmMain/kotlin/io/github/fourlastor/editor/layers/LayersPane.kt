package io.github.fourlastor.editor.layers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.editor.TransparentField
import io.github.fourlastor.editor.icon.MediumIcon
import io.github.fourlastor.editor.icon.SmallIcon
import io.github.fourlastor.editor.state.EntitiesState
import io.github.fourlastor.editor.state.EntityNode
import io.github.fourlastor.editor.state.EntityState
import io.github.fourlastor.editor.state.GroupNode
import io.github.fourlastor.editor.state.ImageNode
import io.github.fourlastor.entity.EntityType
import io.github.fourlastor.entity.EntityUpdater
import io.github.fourlastor.system.Selectable
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.Label

/**
 * Layers panel.
 * Displays a tree of entities, with editable names
 */
@Composable
fun LayersPane(
        entities: EntitiesState,
        modifier: Modifier,
        entityUpdater: EntityUpdater,
        onAddGroup: (parentId: Long) -> Unit,
        onAddImage: (parentId: Long) -> Unit,
        onDeleteNode: (parentId: Long) -> Unit,
) {
    var selectedEntity by remember { mutableStateOf<EntityNode?>(null) }
    val rootNode by remember(entities) { derivedStateOf { entities.asNode() } }

    Column(
            modifier = modifier.fillMaxSize().padding(2.dp)
    ) {
        Row(
                modifier = Modifier.padding(4.dp).height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
        ) {
            Label(
                    text = "Layers", modifier = Modifier.weight(1f), fontSize = 16.sp
            )
            AddEntitiesButtons(selectedEntity, onAddGroup, onAddImage, onDeleteNode)
        }
        EntityTreeView(
                entity = rootNode,
                selectedEntity = selectedEntity,
                onEntitySelected = { it: EntityNode ->
                    selectedEntity = it
                },
                modifier = Modifier.fillMaxHeight(0.7f).verticalScroll(rememberScrollState()).fillMaxWidth(),
                entityUpdater = entityUpdater,
        )
    }
}

@Composable
private fun AddEntitiesButtons(
    selectedEntity: EntityNode?,
    onAddGroup: (parentId: Long) -> Unit,
    onAddImage: (parentId: Long) -> Unit,
    onDeleteNode: (parentId: Long) -> Unit,
) {
    if (selectedEntity == null) {
        return
    }
    AddButton(
            parentId = selectedEntity.entity.id, type = EntityType.DELETE, onEntityAdd = onDeleteNode
    )
    if (selectedEntity !is GroupNode) {
        return
    }
    AddButton(
            parentId = selectedEntity.entity.id, type = EntityType.GROUP, onEntityAdd = onAddGroup
    )
    AddButton(
            parentId = selectedEntity.entity.id, type = EntityType.IMAGE, onEntityAdd = onAddImage
    )
}

@Composable
private fun AddButton(
    parentId: Long,
    type: EntityType,
    onEntityAdd: (parentId: Long) -> Unit,
) {
    ActionButton(
            onClick = { onEntityAdd(parentId) }, modifier = Modifier.padding(4.dp)
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
        onEntityCollapse: (Long) -> Unit,
        modifier: Modifier = Modifier,
        entityUpdater: EntityUpdater,
) {
    when (entity) {
        is GroupNode -> GroupNodeView(entity, selectedEntity, onEntitySelected, onEntityCollapse, modifier, entityUpdater)
        is ImageNode -> ImageNodeView(entity, selectedEntity, onEntitySelected, modifier, entityUpdater)
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
        onEntityCollapse: (Long) -> Unit,
        modifier: Modifier,
        entityUpdater: EntityUpdater,
) {
    Column(
        modifier = modifier,
    ) {
        Selectable(
            modifier = Modifier.fillMaxWidth(),
            selected = group == selectedEntity,
            onSelected = { onEntitySelected(group) },
        ) {

            //remember collapsed

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                // Expand/Collapse button
                AddButton(
                        parentId = group.entity.id, type = EntityType.COLLAPSE, onEntityAdd = onEntityCollapse
                )
                EntityName("icons/group.svg", group.entity, entityUpdater)
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
                        onEntityCollapse,
                        Modifier.fillMaxWidth(),
                        entityUpdater,
                )
            }
        }
    }
}

@Composable
private fun EntityName(
        icon: String,
        entity: EntityState,
        entityUpdater: EntityUpdater,
) {
    SmallIcon(icon)
    TransparentField(
            value = entity.name,
            validator = { it },
            onValueChange = { name -> entityUpdater(entity.id) { it.name(name) } },
            textStyle = TextStyle(fontSize = 16.sp)
    )
}

/**
 * Displays a leaf entity, showing a label with its name.
 */
@Composable
private fun ImageNodeView(
        entity: ImageNode,
        selectedEntity: EntityNode?,
        onEntitySelected: (EntityNode) -> Unit,
        modifier: Modifier,
        entityUpdater: EntityUpdater,
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
                    entityUpdater = entityUpdater,
            )
        }
    }
}
