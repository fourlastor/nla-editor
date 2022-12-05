package io.github.fourlastor.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.entity.Entities
import io.github.fourlastor.entity.Entity
import io.github.fourlastor.entity.EntityNode
import io.github.fourlastor.entity.GroupNode
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.TextField
import io.kanro.compose.jetbrains.expui.control.themedSvgResource
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors

/**
 * Property inspector panel.
 * Displays a tree of entities, and when selected it shows the editable data for the selected entity in a form.
 */
@Composable
fun LayersPane(
    entities: Entities,
    modifier: Modifier,
    onEntityChange: (Entity) -> Unit,
    @Suppress("UNUSED_PARAMETER") onEntityAdd: (parentId: Long) -> Unit,
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
            onEntityChange = onEntityChange,
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
    onEntityChange: (Entity) -> Unit,
) {
    if (entity is GroupNode) {
        GroupNodeView(entity, selectedEntity, onEntitySelected, modifier, onEntityChange)
    } else {
        EntityNodeView(entity, selectedEntity, onEntitySelected, modifier, onEntityChange)
    }
}

val selectedColor = Color(0.3f, 0.5f, 0.8f)

/** Used to make an element selectable. */
@Composable
private fun Selectable(
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(selected: Boolean) -> Unit,
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
        content = { content(selected) },
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
    onEntityChange: (Entity) -> Unit,
) {
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
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                SmallIcon("icons/group.svg")
                Label(
                    text = group.entity.name,
                    fontSize = 18.sp
                )
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
        modifier = modifier
    ) { selected ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            var text by remember(entity.entity.name) { mutableStateOf(entity.entity.name) }
            if (selected) TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                modifier = Modifier
                    .padding(2.dp)
                    .onFocusChanged {
                        if (!it.hasFocus) {
                            onEntityChange(entity.entity.name(text))
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
            )
            else {
                SmallIcon("icons/image.svg")
                Label(
                    text = entity.entity.name,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SmallIcon(icon: String) {
    Image(
        themedSvgResource(icon),
        contentDescription = null,
        modifier = Modifier.size(14.dp),
        colorFilter = ColorFilter.tint(LocalAreaColors.current.text),
    )
}
