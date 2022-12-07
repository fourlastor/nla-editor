package io.github.fourlastor.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fourlastor.editor.state.ViewState
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.ComboBox
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.SegmentedButton

@Composable
fun AnimationMode(state: ViewState.AnimationState, onAnimationModeChange: (ViewState.AnimationState) -> Unit) {
    Row(
        modifier = Modifier
            .height(40.dp).padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Label("Animation mode: ")
        val selectedIndex by remember(state) { derivedStateOf { if (state is ViewState.Enabled) 1 else 0 } }
        SegmentedButton(2, selectedIndex, {
            onAnimationModeChange(if (it == 0) ViewState.Disabled else ViewState.Selecting)
        }) {
            when (it) {
                0 -> Label("Disabled")
                1 -> Label("Enabled")
                else -> error("Invalid index")
            }
        }
        if (state is ViewState.Enabled) {
            Label("Animation: ")
            val comboBoxItems = remember {
                (0 until 10).map { "Animation $it" }
            }
            val comboBoxSelection by remember(state) {
                derivedStateOf {
                    when (state) {
                        is ViewState.Selecting -> ""
                        is ViewState.Selected -> state.name
                    }
                }
            }

            ComboBox(comboBoxItems, comboBoxSelection, {
                onAnimationModeChange(ViewState.Selected(it))
            }, modifier = Modifier.width(150.dp), menuModifier = Modifier.width(150.dp))

            ActionButton(
                onClick = {}
            ) {
                Label("New animation")
            }
        }
    }
}
