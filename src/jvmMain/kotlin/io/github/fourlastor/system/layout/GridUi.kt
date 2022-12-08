package io.github.fourlastor.system.layout

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.fourlastor.editor.DraggableHandle

/** 2x2 grid that can be resized by dragging the separators. */
@Composable
fun GridUi(
    modifier: Modifier,
    topLeft: @Composable BoxScope.() -> Unit,
    topRight: @Composable BoxScope.() -> Unit,
    bottomLeft: @Composable BoxScope.() -> Unit,
    bottomRight: @Composable BoxScope.() -> Unit,
) = BoxWithConstraints(
    modifier = modifier.fillMaxSize()
) {
    var horizontalCutPoint by remember { mutableStateOf(0.5f) }
    var verticalCutPoint by remember { mutableStateOf(0.7f) }
    val width = constraints.maxWidth
    val height = constraints.maxHeight

    Column(
        modifier = Modifier.matchParentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(horizontalCutPoint)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(verticalCutPoint),
                content = topLeft
            )
            DraggableHandle(Orientation.Vertical) { verticalCutPoint += it.x / width }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                content = topRight
            )
        }
        DraggableHandle(Orientation.Horizontal) { horizontalCutPoint += it.y / height }
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(verticalCutPoint),
                content = bottomLeft
            )
            DraggableHandle(Orientation.Vertical) { verticalCutPoint += it.x / width }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                content = bottomRight
            )
        }
    }
}
