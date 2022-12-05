package io.github.fourlastor.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun DraggableHandle(
    orientation: Orientation,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    size: Dp = 4.dp,
    onDrag: (Offset) -> Unit,
) {
    color.takeOrElse { LocalAreaColors.current.startBorderColor }
    Spacer(
        modifier = modifier
            .background(color)
            .run {
                if (orientation == Orientation.Vertical) {
                    width(size).fillMaxHeight()
                } else {
                    height(size).fillMaxWidth()
                }
            }
            .pointerHoverIcon(if (orientation == Orientation.Vertical) verticalResize else horizontalResize)
            .onDrag(onDrag = onDrag)
    )
}
