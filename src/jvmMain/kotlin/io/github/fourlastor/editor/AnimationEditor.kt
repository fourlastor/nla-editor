package io.github.fourlastor.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.fourlastor.entity.*
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors
import io.kanro.compose.jetbrains.expui.style.areaBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimationEditor() {
    var state by rememberEditorState()
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PreviewPane(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
            )
            Spacer(Modifier.background(LocalAreaColors.current.startBorderColor).height(1.dp).fillMaxWidth())
            Timeline(
                modifier = Modifier
                    .fillMaxSize()
                    .areaBackground()
                    .zIndex(2f),
            )
        }
        Spacer(Modifier.background(LocalAreaColors.current.startBorderColor).width(1.dp).fillMaxHeight())
        PropertiesPane(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .areaBackground()
                .zIndex(2f),
            onEntityChange = {
                val entities = state.entities.update(it)
                state = state.copy(entities = entities)
            },
        )
    }
}

@Composable
fun rememberEditorState() = remember {
    mutableStateOf(
        EditorState(
            entities = Entities()
                .image(0, "Player big", "player.png")
                .image(
                    0,
                    "Player small",
                    "player.png",
                    transform = Transform.IDENTITY.copy(rotation = 90f, offset = Offset(4f, 5f), scale = 0.4f)
                ),
        )
    )
}

data class EditorState(
    val entities: Entities,
)
