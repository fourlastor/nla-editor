package io.github.fourlastor.editor

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.kanro.compose.jetbrains.expui.control.Icon

@Composable
fun KeyFrame(
    modifier: Modifier = Modifier,
) {
    Icon(
        "icons/diamond.svg",
        modifier = modifier
            .size(20.dp),
        colorFilter = ColorFilter.tint(Color(0xFF9c3aef))
    )
}
