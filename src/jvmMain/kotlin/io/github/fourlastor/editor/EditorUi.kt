package io.github.fourlastor.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.fourlastor.data.Animation
import io.github.fourlastor.data.Animations
import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.editor.animationmode.EditorMode
import io.github.fourlastor.editor.layers.LayersPane
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
        entities: Entities,
        animations: Animations,
        entityUpdater: EntityUpdater,
        onAddGroup: (parentId: Long) -> Unit,
        onDeleteEntity: (parentId: Long) -> Unit,
        onAddImage: (parentId: Long) -> Unit,
        onAddAnimation: (name: String, duration: Duration) -> Unit,
        onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
        onUpdateAnimation: (animationId: Long, update: (Animation) -> Animation) -> Unit,
) {
    var viewState: ViewState by remember { mutableStateOf(ViewState.initial()) }
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
                            entities = entities,
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
                        val animationState = viewState.animations
                        if (animationState is ViewState.Enabled) {
                            AnimationPropertiesEditor(
                                    viewState = animationState,
                                    animations = animations,
                                    onSelectAnimation = {
                                        viewState = viewState.copy(animations = ViewState.Selected(it))
                                    },
                                    onUpdateAnimation = onUpdateAnimation,
                                    onAddAnimation = onAddAnimation,
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
                                        duration = animations[animationState.id].duration,
                                )
                            }
                        }
                    }
                },
                bottomRight = { listState ->
                    PropertiesPane(
                            propertyNamesListState = listState,
                            modifier = Modifier.matchParentSize().padding(end = 4.dp),
                            viewState = viewState,
                            entityUpdater = entityUpdater,
                            entities = entities,
                            onAddKeyFrame = onAddKeyFrame,
                    )
                }
        )
    }
}
