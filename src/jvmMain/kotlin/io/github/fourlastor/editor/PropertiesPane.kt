package io.github.fourlastor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.entity.Entities
import io.github.fourlastor.entity.Entity
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.style.areaBackground
import io.kanro.compose.jetbrains.expui.theme.DarkTheme

@Composable
fun PropertiesPane(
    propertyNamesListState: LazyListState,
    entities: Entities,
    modifier: Modifier = Modifier,
    onEntityChange: (Entity) -> Unit,
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
                        { onEntityChange(entity.x(it)) }
                    )
                    Property(
                        "Y",
                        entity.transform.y.toString(),
                        { it.toFloatOrNull() ?: 0f },
                        { onEntityChange(entity.y(it)) }
                    )
                    Property(
                        "Rotation",
                        entity.transform.rotation.toString(),
                        { it.toFloatOrNull() ?: 0f },
                        { onEntityChange(entity.rotation(it)) }
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
    entity: Entity,
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
