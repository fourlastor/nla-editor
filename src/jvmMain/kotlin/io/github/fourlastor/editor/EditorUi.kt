package io.github.fourlastor.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.fourlastor.editor.layers.LayersPane
import io.github.fourlastor.entity.Entities
import io.kanro.compose.jetbrains.expui.style.areaBackground
import kotlinx.coroutines.launch
import org.jetbrains.skiko.Cursor

/**
 *  Main UI for the editor, contains the [PreviewPane], [Timeline], and [PropertiesPane].
 *  [entities] entities to display in the editor
 *  [onEntitiesChange] callback to update [entities]
 *  [onAddGroup] callback to request adding a new group
 *  [onAddImage] callback to request adding a new image
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun EditorUi(
    entities: Entities,
    onEntitiesChange: (Entities) -> Unit,
    onAddGroup: (parentId: Long) -> Unit,
    onAddImage: (parentId: Long) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        var horizontalCutPoint by remember { mutableStateOf(0.5f) }
        var verticalCutPoint by remember { mutableStateOf(0.7f) }
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        Column(
            modifier = Modifier.matchParentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(horizontalCutPoint)
            ) {
                PreviewPane(
                    entities = entities,
                    modifier = Modifier
                        .fillMaxWidth(verticalCutPoint)
                        .fillMaxHeight(),
                )
                DraggableHandle(Orientation.Vertical) { verticalCutPoint += it.x / width }
                LayersPane(
                    entities = entities,
                    modifier = Modifier
                        .fillMaxSize()
                        .areaBackground()
                        .zIndex(2f),
                    onEntityChange = {
                        onEntitiesChange(entities.update(it))
                    },
                    onAddGroup = onAddGroup,
                    onAddImage = onAddImage,
                )
            }
            DraggableHandle(Orientation.Horizontal) { horizontalCutPoint += it.y / height }
            val propertyKeysListState = rememberLazyListState()
            val propertyNamesListState = rememberLazyListState()
            val scope = rememberCoroutineScope()
            val scrollState = rememberScrollableState { delta ->
                scope.launch {
                    propertyKeysListState.scrollBy(-delta)
                    propertyNamesListState.scrollBy(-delta)
                }
                delta
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .scrollable(scrollState, orientation = Orientation.Vertical)
            ) {
                Timeline(
                    entities = entities,
                    propertyListState = propertyKeysListState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(verticalCutPoint)
                        .padding(start = 4.dp)
                        .areaBackground()
                        .zIndex(2f),
                )
                DraggableHandle(Orientation.Vertical) { verticalCutPoint += it.x / width }
                PropertiesPane(
                    propertyNamesListState = propertyNamesListState,
                    entities = entities,
                    modifier = Modifier.padding(end = 4.dp),
                    onEntityChange = { onEntitiesChange(entities.update(it)) }
                )
            }
        }
    }
}

val horizontalResize = PointerIcon(Cursor(Cursor.N_RESIZE_CURSOR))
val verticalResize = PointerIcon(Cursor(Cursor.W_RESIZE_CURSOR))
