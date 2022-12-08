package io.github.fourlastor.editor.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.data.Animations
import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.editor.KeyFrame
import io.github.fourlastor.editor.TransparentField
import io.github.fourlastor.editor.state.ViewState
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PropertiesPane(
    propertyNamesListState: LazyListState,
    entities: Entities,
    viewState: ViewState,
    modifier: Modifier = Modifier,
    entityUpdater: EntityUpdater,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
    trackPosition: () -> Float,
    animations: Animations,
) {
    val state by remember(entities, animations, viewState) {
        derivedStateOf {
            toPropertiesState(
                entities,
                animations,
                viewState
            )
        }
    }
    PropertiesPaneUi(
        modifier = modifier,
        propertyNamesListState = propertyNamesListState,
        state = state,
        entityUpdater = entityUpdater,
        onAddKeyFrame = onAddKeyFrame,
        trackPosition = trackPosition,
    )
}

@Composable
private fun PropertiesPaneUi(
    modifier: Modifier,
    propertyNamesListState: LazyListState,
    state: PropertiesState,
    entityUpdater: EntityUpdater,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
    trackPosition: () -> Float,
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            state = propertyNamesListState,
            userScrollEnabled = false,
        ) {
            for (entity in state.entities) {
                item(key = "e/${entity.id}") {
                    EntityName(
                        entity, modifier = Modifier
                            .height(44.dp)
                            .fillMaxWidth(0.3f)
                    )
                }
                for (property in entity.properties) {
                    item(key = "p/${property.id}") {
                        Property(
                            property = property,
                            entityUpdater = entityUpdater,
                            onAddKeyFrame = onAddKeyFrame,
                            trackPosition = trackPosition,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Property(
    property: PropertiesState.Property,
    entityUpdater: EntityUpdater,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
    trackPosition: () -> Float
) {
    when (property) {
        is PropertiesState.FloatProperty -> PropertyField(
            label = property.label,
            value = property.value.toString(),
            validator = { it.toFloatOrNull() ?: 0f },
            onValueChange = { property.updater.update(it, entityUpdater) }
        )

        is PropertiesState.ReadonlyFloatProperty -> PropertyReadOnly(
            label = property.label,
            value = property.value.toString(),
        )

        is PropertiesState.AnimatedFloatProperty -> PropertyAnimated(
            property = property,
            validator = { it.toFloatOrNull() ?: 0f },
            onAddKeyFrame = onAddKeyFrame,
            trackPosition = trackPosition,
        )
    }
}

@Composable
private fun PropertyTrack(
    label: String,
    modifier: Modifier = Modifier,
    keyframe: @Composable RowScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .height(40.dp)
            .background(DarkTheme.Grey1)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Label(
            text = "$label ",
            Modifier.fillMaxWidth(0.3f),
            textAlign = TextAlign.End
        )
        content()
        keyframe()
    }
}

@Composable
private fun <T> PropertyField(
    label: String,
    value: String,
    validator: (String) -> T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    PropertyTrack(
        label = label,
        modifier = modifier,
    ) {
        TransparentField(
            value = value,
            onValueChange = onValueChange,
            validator = validator,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PropertyAnimated(
    validator: (String) -> Float,
    property: PropertiesState.AnimatedFloatProperty,
    modifier: Modifier = Modifier,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
    trackPosition: () -> Float
) {
    var value by remember { mutableStateOf(property.value) }
    PropertyTrack(
        label = property.label,
        modifier = modifier,
        keyframe = {
            KeyFrame(false, modifier = Modifier.clickable {
                onAddKeyFrame(
                    property.animationId,
                    property.entityId,
                    property.id,
                    value,
                    (property.trackLength * trackPosition().toDouble()).inWholeMilliseconds.milliseconds,
                )
            })
        }
    ) {
        TransparentField(
            value = value.toString(),
            onValueChange = { value = it },
            validator = validator,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PropertyReadOnly(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    PropertyTrack(
        label = label,
        modifier = modifier,
    ) {
        Label(
            text = value,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EntityName(
    entity: PropertiesState.Entity,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Label(
            text = entity.name,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
