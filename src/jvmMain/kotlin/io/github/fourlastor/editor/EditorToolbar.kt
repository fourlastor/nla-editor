package io.github.fourlastor.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.github.fourlastor.editor.icon.SmallIcon
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.style.areaBackground

/** Toolbar on top of the editor, it contains save and load buttons. */
@Composable
fun EditorToolbar(onLoad: () -> Unit, onSave: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .zIndex(2f)
            .areaBackground(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ToolbarButton("Save", "icons/save.svg", onSave)
        ToolbarButton("Load", "icons/load.svg", onLoad)
    }
}

@Composable
private fun ToolbarButton(text: String, icon: String, onClick: () -> Unit) {
    ActionButton(
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp),
        ) {
            SmallIcon(icon = icon)
            Label(text = text, fontSize = 12.sp, fontWeight = FontWeight.Light)
        }
    }
}
