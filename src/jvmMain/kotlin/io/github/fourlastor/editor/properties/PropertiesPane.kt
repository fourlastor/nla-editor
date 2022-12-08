package io.github.fourlastor.editor.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.data.Entities
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.editor.KeyFrame
import io.github.fourlastor.editor.TransparentField
import io.github.fourlastor.editor.state.ViewState
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.style.areaBackground
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun PropertiesPane(
    propertyNamesListState: LazyListState,
    entities: Entities,
    viewState: ViewState,
    modifier: Modifier = Modifier,
    entityUpdater: EntityUpdater,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
) {
    val state by remember(entities, viewState) { derivedStateOf { toPropertiesState(entities, viewState) } }
    PropertiesPaneUi(
        modifier = modifier,
        propertyNamesListState = propertyNamesListState,
        state = state,
        entityUpdater = entityUpdater,
        onAddKeyFrame = onAddKeyFrame,
    )
}

@Composable
private fun PropertiesPaneUi(
    modifier: Modifier,
    propertyNamesListState: LazyListState,
    state: PropertiesState,
    entityUpdater: EntityUpdater,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxHeight()
            .fillMaxWidth()
            .areaBackground()
    ) {
        Spacer(
            modifier = Modifier.height(54.dp)
                .fillMaxWidth()
        )
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
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit
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
private fun <T> PropertyAnimated(
    validator: (String) -> T,
    property: PropertiesState.AnimatedFloatProperty,
    modifier: Modifier = Modifier,
    onAddKeyFrame: (animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration) -> Unit
) {
    PropertyTrack(
        label = property.label,
        modifier = modifier,
        keyframe = {
            KeyFrame(false, modifier = Modifier.clickable {
                onAddKeyFrame(
                    property.animationId,
                    property.entityId,
                    property.id,
                    property.value,
                    2.seconds,
                )
            })
        }
    ) {
        TransparentField(
            value = property.value.toString(),
            onValueChange = { },
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
