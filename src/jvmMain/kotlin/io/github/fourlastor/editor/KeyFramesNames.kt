package io.github.fourlastor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.fourlastor.entity.Entities
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.style.areaBackground

@Composable
fun KeyFramesNames(
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
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(40.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        Label(entity.name, color = Color.Black)
                    }
                    repeat(3) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(40.dp)
                                .background(Color.LightGray),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                        }
                    }
                }
            }
        }
    }
}
