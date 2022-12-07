package io.github.fourlastor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.data.EntityUpdater
import io.github.fourlastor.editor.state.EntitiesState
import io.github.fourlastor.editor.state.EntityState
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.style.areaBackground
import io.kanro.compose.jetbrains.expui.theme.DarkTheme

@Composable
fun PropertiesPane(
        propertyNamesListState: LazyListState,
        entities: EntitiesState,
        modifier: Modifier = Modifier,
        entityUpdater: EntityUpdater,
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = propertyNamesListState,
            userScrollEnabled = false,
        ) {
            items(
                count = entities.entities.size,
                key = { entities.entities[it].id }
            ) { entityIndex ->
                val entity = entities.entities[entityIndex]
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    EntityName(entity, modifier = Modifier.fillMaxWidth(0.3f))
                    Property(
                        "X",
                        entity.transform.x.toString(),
                        { it.toFloatOrNull() ?: 0f },
                            { x -> entityUpdater(entity.id) { it.x(x) } }
                    )
                    Property(
                        "Y",
                        entity.transform.y.toString(),
                        { it.toFloatOrNull() ?: 0f },
                            { y -> entityUpdater(entity.id) { it.y(y) } }
                    )
                    Property(
                        "Rotation",
                        entity.transform.rotation.toString(),
                        { it.toFloatOrNull() ?: 0f },
                            { rotation -> entityUpdater(entity.id) { it.rotation(rotation) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> Property(
    label: String,
    value: String,
    validator: (String) -> T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
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
            modifier.fillMaxWidth(0.3f),
            textAlign = TextAlign.End
        )
        TransparentField(
            value = value,
            onValueChange = onValueChange,
            validator = validator,
            modifier = Modifier.weight(1f)
        )
        KeyFrame()
    }
}

@Composable
private fun EntityName(
        entity: EntityState,
        modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(40.dp)
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
