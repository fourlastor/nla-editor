package io.github.fourlastor.editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.kanro.compose.jetbrains.expui.control.themedSvgResource

@Composable
fun KeyFrame(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(20.dp),
        contentAlignment = Alignment.Center
    ) {
        val selectedColor = remember { Color(0xFF9c3aef) }
        val deselectedColor = remember { Color.LightGray }
        Image(
            themedSvgResource("icons/diamond.svg"),
            contentDescription = "keyframe",
            modifier = Modifier
                .size(12.dp),
            colorFilter = ColorFilter.tint(if (selected) selectedColor else deselectedColor)
        )
    }
}
