package io.github.fourlastor.system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

val selectedColor = Color(0.3f, 0.5f, 0.8f)

/** Used to make an element selectable. */
@Composable
fun Selectable(
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(selected: Boolean) -> Unit,
) {
    val bgColor = if (selected) {
        selectedColor
    } else Color.Unspecified
    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onSelected,
            )
            .background(bgColor),
        content = { content(selected) },
    )
}
