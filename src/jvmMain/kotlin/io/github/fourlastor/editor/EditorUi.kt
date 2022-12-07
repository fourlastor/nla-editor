package io.github.fourlastor.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.editor.layers.LayersPane
import io.github.fourlastor.editor.state.EditorState
import io.kanro.compose.jetbrains.expui.style.areaBackground
import kotlinx.coroutines.launch
import org.jetbrains.skiko.Cursor

/**
 *  Main UI for the editor, contains the [PreviewPane], [Timeline], and [PropertiesPane].
 *  [entityUpdater] pass an ID and a lambda to update an entity
 *  [onAddGroup] callback to request adding a new group
 *  [onAddImage] callback to request adding a new image
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun EditorUi(
    state: EditorState,
    entityUpdater: EntityUpdater,
    onAddGroup: (parentId: Long) -> Unit,
    onDeleteNode: (parentId: Long) -> Unit,
    onAddImage: (parentId: Long) -> Unit,
    onToggleAnimationMode: (enabled: Boolean) -> Unit,
) {
    val entities = state.entities
    val viewState = state.viewState
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimationModeToggle(viewState.animationsEnabled, onToggleAnimationMode)

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
                        entityUpdater = entityUpdater,
                        onAddGroup = onAddGroup,
                        onAddImage = onAddImage,
                        onDeleteNode = onDeleteNode,
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
                    if (viewState.animationsEnabled) {
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
                    } else {
                        Spacer(modifier = Modifier.fillMaxWidth(verticalCutPoint).fillMaxHeight())
                    }
                    DraggableHandle(Orientation.Vertical) { verticalCutPoint += it.x / width }
                    PropertiesPane(
                        propertyNamesListState = propertyNamesListState,
                        entities = entities,
                        modifier = Modifier.padding(end = 4.dp),
                        entityUpdater = entityUpdater
                    )
                }
            }
        }
    }
}

val horizontalResize = PointerIcon(Cursor(Cursor.N_RESIZE_CURSOR))
val verticalResize = PointerIcon(Cursor(Cursor.W_RESIZE_CURSOR))
