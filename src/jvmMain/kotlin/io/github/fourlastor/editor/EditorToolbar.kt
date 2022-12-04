package io.github.fourlastor.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.Icon
import io.kanro.compose.jetbrains.expui.style.areaBackground
import io.kanro.compose.jetbrains.expui.window.MainToolBarScope

@Composable
fun MainToolBarScope.EditorToolbar(onLoad: () -> Unit, onSave: () -> Unit) {
    Row(
        modifier = Modifier
            .mainToolBarItem(alignment = Alignment.End)
            .fillMaxSize()
            .zIndex(2f)
            .areaBackground(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        ActionButton(
            onClick = onLoad
        ) {
            Icon(resource = "icons/file_open.svg")
        }
        ActionButton(
            onClick = onSave
        ) {
            Icon(resource = "icons/save.svg")
        }
    }
}
