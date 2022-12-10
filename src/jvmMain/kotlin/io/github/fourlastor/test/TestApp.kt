package io.github.fourlastor.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.fourlastor.system.layout.GridUi

@Composable
fun TestApp() {
    GridUi(
        modifier = Modifier.fillMaxSize(),
        topLeft = {
            FilledWith(Color.Red)
        },
        topRight = {
            FilledWith(Color.Green)
        },
        bottomLeft = {
            FilledWith(Color.Yellow)
        },
        bottomRight = {
            FilledWith(Color.Blue)
        }
    )
}

@Composable
private fun BoxScope.FilledWith(color: Color) {
    Box(Modifier.background(color).matchParentSize()) {

    }
}
