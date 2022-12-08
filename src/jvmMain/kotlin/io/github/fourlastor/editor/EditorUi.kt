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
import io.github.fourlastor.data.Animations
import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.editor.animationmode.AnimationMode
import io.github.fourlastor.editor.layers.LayersPane
import io.github.fourlastor.editor.properties.PropertiesPane
import io.github.fourlastor.editor.state.ViewState
import io.kanro.compose.jetbrains.expui.style.areaBackground
import kotlinx.coroutines.launch
import org.jetbrains.skiko.Cursor
import kotlin.time.Duration

/**
 *  Main UI for the editor, contains the [PreviewPane], [Timeline], and [PropertiesPane].
 *  [entityUpdater] pass an ID and a lambda to update an entity
 *  [onAddGroup] callback to request adding a new group
 *  [onAddImage] callback to request adding a new image
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun EditorUi(
    entities: Entities,
    animations: Animations,
    entityUpdater: EntityUpdater,
    onAddGroup: (parentId: Long) -> Unit,
    onDeleteEntity: (parentId: Long) -> Unit,
    onAddImage: (parentId: Long) -> Unit,
    onCreateAnimation: (name: String, duration: Duration) -> Unit,
) {
    var viewState: ViewState by remember { mutableStateOf(ViewState.initial()) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AnimationMode(
            onAnimationToggle = {
                viewState =
                    if (it) viewState.copy(animations = ViewState.Selecting)
                    else viewState.copy(animations = ViewState.Disabled)
            },
            onAnimationSelected = {
                viewState = viewState.copy(animations = ViewState.Selected(it))
            },
            onCreateAnimation = onCreateAnimation,
            animations = animations,
            animationState = viewState.animations,
        )

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
                        onDeleteEntity = onDeleteEntity,
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
                    val animationState = viewState.animations
                    if (animationState is ViewState.Selected) {
                        Timeline(
                            entities = entities,
                            propertyListState = propertyKeysListState,
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(verticalCutPoint)
                                .padding(start = 4.dp)
                                .areaBackground()
                                .zIndex(2f),
                            animationId = animationState.id,
                            animations = animations,
                        )
                    } else {
                        Spacer(modifier = Modifier.fillMaxWidth(verticalCutPoint).fillMaxHeight())
                    }
                    DraggableHandle(Orientation.Vertical) { verticalCutPoint += it.x / width }
                    PropertiesPane(
                        propertyNamesListState = propertyNamesListState,
                        modifier = Modifier.padding(end = 4.dp),
                        viewState = viewState,
                        entityUpdater = entityUpdater,
                        entities = entities,
                    )
                }
            }
        }
    }
}

val horizontalResize = PointerIcon(Cursor(Cursor.N_RESIZE_CURSOR))
val verticalResize = PointerIcon(Cursor(Cursor.W_RESIZE_CURSOR))
