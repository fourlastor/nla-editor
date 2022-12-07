package io.github.fourlastor.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.SegmentedButton

@Composable
fun AnimationModeToggle(enabled: Boolean, onEnabledChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val selectedIndex by remember(enabled) { derivedStateOf { if (enabled) 1 else 0 } }
        SegmentedButton(2, selectedIndex, {
            onEnabledChange(it == 1)
        }) {
            when (it) {
                0 -> Label("Disabled")
                1 -> Label("Enabled")
                else -> error("Invalid index")
            }
        }
        Label("Animation mode")
    }
}
