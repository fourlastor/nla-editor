package io.github.fourlastor.editor

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import io.github.fourlastor.entity.Entity
import io.github.fourlastor.entity.Group
import io.github.fourlastor.entity.Image

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun AnimationEditor() {
    val state = rememberEditorState()
    MaterialTheme {
        Row {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(),
            ) {
                PreviewPane(
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f),
                )
                Timeline(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0.8f, 0.7f, 0.3f)),
                )
            }
            PropertiesPane(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun rememberEditorState() = remember {
    EditorState(
        entity = Group(
            entities = listOf(Image(useResource("player.png") { loadImageBitmap(it) }))
        ),
    )
}

data class EditorState(
    val entity: Entity,
)
