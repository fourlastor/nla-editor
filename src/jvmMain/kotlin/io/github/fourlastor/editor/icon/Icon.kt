package io.github.fourlastor.editor.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import io.kanro.compose.jetbrains.expui.control.themedSvgResource
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors

@Composable
fun Icon(
    icon: String,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Image(
        themedSvgResource(icon),
        contentDescription = null,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(LocalAreaColors.current.text),
    )
}
