package io.github.fourlastor.toolbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fourlastor.editor.icon.SmallIcon
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.Label

@Composable
fun ToolbarButton(text: String, icon: String, onClick: () -> Unit) {
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
