package io.github.fourlastor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.entity.Entities
import io.github.fourlastor.entity.Entity
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.TextField
import io.kanro.compose.jetbrains.expui.style.areaBackground
import io.kanro.compose.jetbrains.expui.theme.DarkTheme

@Composable
fun KeyFramesValues(
    propertyNamesListState: LazyListState,
    entities: Entities,
    modifier: Modifier = Modifier,
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
            ) {
                val entity = entities.entities[it]
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    EntityName(entity)
                    repeat(3) {
                        Property {
                            Label("x ")
                            TextField(
                                value = "",
                                onValueChange = {},
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Property(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(40.dp)
            .background(DarkTheme.Grey1)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        content = content,
        verticalAlignment = Alignment.CenterVertically
    )
}

@Composable
private fun EntityName(entity: Entity) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Label(text = entity.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
