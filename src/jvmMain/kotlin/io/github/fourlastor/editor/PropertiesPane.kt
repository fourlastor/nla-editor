package io.github.fourlastor.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.entity.Entity
import io.github.fourlastor.entity.Group
import io.github.fourlastor.entity.Image
import io.github.fourlastor.entity.Transform
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.TextField

@Composable
fun PropertiesPane(
    state: EditorState,
    modifier: Modifier,
    onEntityChange: (Entity) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(2.dp)
            .verticalScroll(rememberScrollState())
    ) {
        PropertyEditor(
            entity = state.entity,
            onEntityChange = onEntityChange,
        )
    }
}

@Composable
fun PropertyEditor(entity: Entity, onEntityChange: (Entity) -> Unit) {
    Column {
        Label(entity.name, fontSize = 12.sp)
        TransformEditor(
            transform = entity.transform,
            onXChange = { onEntityChange(entity.x(it)) },
            onYChange = { onEntityChange(entity.y(it)) },
            onRotationChange = { onEntityChange(entity.rotation(it)) }
        )
        when (entity) {
            is Group -> GroupEditor(entity, onEntityChange)
            is Image -> ImageEditor(entity)
        }
    }
}

@Composable
fun TransformEditor(
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
//        label = { Text(label) },
        )
    }
}

@Composable
fun GroupEditor(group: Group, onEntityChange: (Entity) -> Unit) = group.run {
    Column(modifier = Modifier.padding(start = 12.dp)) {
        entities.forEachIndexed { originalIndex, entity ->
            PropertyEditor(
                entity = entity, onEntityChange = {
                    onEntityChange(group.copy(entities = group.entities.mapIndexed { i, e ->
                        if (i == originalIndex) {
                            it
                        } else {
                            e
                        }
                    }))
                }
            )
        }
    }
}

@Composable
fun ImageEditor(
    @Suppress("UNUSED_PARAMETER") image: Image, // one day
) {
    Label("TODO")
}
