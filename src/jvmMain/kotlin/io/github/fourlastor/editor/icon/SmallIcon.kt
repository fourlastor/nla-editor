package io.github.fourlastor.editor.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SmallIcon(
    icon: String,
    modifier: Modifier = Modifier,
) = Icon(icon, 14.dp, modifier)
