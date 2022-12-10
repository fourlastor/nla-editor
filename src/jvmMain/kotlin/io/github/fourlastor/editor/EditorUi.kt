package io.github.fourlastor.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.fourlastor.data.Animation
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.data.LoadableProject
import io.github.fourlastor.editor.animationmode.EditorMode
import io.github.fourlastor.editor.layers.LayersPane
import io.github.fourlastor.editor.properties.AnimationPropertiesEditor
import io.github.fourlastor.editor.properties.PropertiesPane
import io.github.fourlastor.editor.state.ViewState
import io.github.fourlastor.editor.timeline.Timeline
import io.github.fourlastor.system.layout.SyncBottomScrollUi
import io.kanro.compose.jetbrains.expui.style.areaBackground
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
    project: LoadableProject.Loaded,
    entityUpdater: EntityUpdater,
    onAddGroup: (parentId: Long) -> Unit,
    onDeleteEntity: (parentId: Long) -> Unit,
    onAddImage: (parentId: Long) -> Unit,
    onAddAnimation: (name: String, duration: Duration) -> Unit,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
    onUpdateAnimation: (animationId: Long, update: (Animation) -> Animation) -> Unit,
) {
    val entities = project.entities
    val animations = project.animations
    var viewState: ViewState by remember { mutableStateOf(ViewState.initial()) }
    val animationPropertiesHeight = remember { 30.dp }
    val timelineScrollbarHeight = remember { 4.dp }
    val timeTrackHeight = remember { 50.dp }
    val timeIndicatorHeight = remember { timeTrackHeight + timelineScrollbarHeight }
    val animationState = viewState.animations
    fun seek(animationId: Long, position: Duration) {
        viewState = viewState.copy(animations = ViewState.Selected(animationId, position))
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        EditorMode(
            onToggle = {
                viewState =
                    if (it) viewState.copy(animations = ViewState.Selecting)
                    else viewState.copy(animations = ViewState.Disabled)
            },
            animationState = viewState.animations,
        )
        SyncBottomScrollUi(
            modifier = Modifier.weight(1f),
            topLeft = {
                PreviewPane(
                    project = project,
                    modifier = Modifier.matchParentSize(),
                )
            },
            topRight = {
                LayersPane(
                    entities = entities,
                    modifier = Modifier.matchParentSize(),
                    entityUpdater = entityUpdater,
                    onAddGroup = onAddGroup,
                    onAddImage = onAddImage,
                    onDeleteEntity = onDeleteEntity,
                )

            },
            bottomLeft = { listState ->
                Column(modifier = Modifier.matchParentSize()) {
                    if (animationState is ViewState.Enabled) {
                        AnimationPropertiesEditor(
                            viewState = animationState,
                            animations = animations,
                            onSelectAnimation = {
                                viewState = viewState.copy(animations = ViewState.Selected(it))
                            },
                            onUpdateAnimation = onUpdateAnimation,
                            onAddAnimation = onAddAnimation,
                            modifier = Modifier.height(animationPropertiesHeight),
                            onSeek = { animationId, position -> seek(animationId, position) }
                        )
                        if (animationState is ViewState.Selected) {
                            Timeline(
                                entities = entities,
                                propertyListState = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 4.dp)
                                    .areaBackground()
                                    .zIndex(2f),
                                animationId = animationState.id,
                                animations = animations,
                                scrollbarHeight = timelineScrollbarHeight,
                                timeTrackHeight = timeTrackHeight,
                                animationState = animationState,
                            ) { seek(animationState.id, it) }
                        }
                    }
                }
            },
            bottomRight = { listState ->
                Column(
                    modifier = Modifier.matchParentSize()
                ) {
                    Spacer(
                        modifier = Modifier
                            .height(animationPropertiesHeight + timeIndicatorHeight)
                            .background(Color.Red)
                    )
                    PropertiesPane(
                        propertyNamesListState = listState,
                        modifier = Modifier.weight(1f)
                            .fillMaxWidth()
                            .padding(end = 4.dp),
                        viewState = viewState,
                        entityUpdater = entityUpdater,
                        entities = entities,
                        onAddKeyFrame = onAddKeyFrame,
                        animations = animations,
                    )
                }
            }
        )
    }
}
