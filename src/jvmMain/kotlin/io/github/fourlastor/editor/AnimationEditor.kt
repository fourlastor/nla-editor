package io.github.fourlastor.editor

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun AnimationEditor() {
    MaterialTheme {
        Row {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(),
            ) {
                PreviewPane(
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
