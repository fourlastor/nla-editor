package io.github.fourlastor.editor.animationmode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fourlastor.editor.state.ViewState
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.SegmentedButton

@Composable
fun EditorMode(
    onToggle: (enabled: Boolean) -> Unit,
    animationState: ViewState.AnimationState,
) {
    Box(
        modifier = Modifier
            .height(40.dp).padding(4.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val selectedIndex by remember(key1 = animationState) { derivedStateOf { if (animationState is ViewState.Enabled) 1 else 0 } }
        SegmentedButton(2, selectedIndex, {
            onToggle(it != 0)
        }) {
            when (it) {
                0 -> Label("Design")
                1 -> Label("Animation")
                else -> error("Invalid index")
            }
        }
    }
}
