package io.github.fourlastor.system.layout

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.roundedCorners(
    color: Color,
    roundCorners: RoundCorners = RoundCorners.Both,
    cornerRadius: Dp = 2.dp,
) = drawWithCache {
    onDrawBehind {
        val rectSize = Size(size.width, size.height)
        val topLeft = Offset.Zero
        drawRoundRect(
            color,
            size = rectSize,
            topLeft = topLeft,
            cornerRadius = CornerRadius(cornerRadius.toPx())
        )
        drawSharpCorners(rectSize, cornerRadius, roundCorners, color, topLeft)
    }
}

private fun DrawScope.drawSharpCorners(
    rectSize: Size,
    cornerRadius: Dp,
    roundCorners: RoundCorners,
    color: Color,
    topLeft: Offset
) {
    val size = rectSize.copy(width = (cornerRadius * 2).toPx())
    if (!roundCorners.start) {
        drawRect(
            color = color,
            size = size,
            topLeft = topLeft
        )
    }
    if (!roundCorners.end) {
        drawRect(
            color = color,
            size = size,
            topLeft = topLeft.copy(x = this.size.width - size.width)
        )
    }
}
