package io.github.fourlastor.newProject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import io.github.fourlastor.system.FileSaveDialog
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.PrimaryButton

@Composable
fun NewProject(
    onNewProject: (path: String) -> Unit,
) {
    var dialogVisible by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PrimaryButton(onClick = { dialogVisible = true }) {
            Label("Create new project", fontSize = 30.sp)
        }
    }
    if (dialogVisible) {
        FileSaveDialog(
            onCloseRequest = {
                if (it != null) {
                    onNewProject(it.absolutePath)
                } else {
                    dialogVisible = false
                }
            }
        )
    }
}
