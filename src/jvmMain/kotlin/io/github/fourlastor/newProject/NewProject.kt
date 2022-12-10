package io.github.fourlastor.newProject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
    onLoadProject: () -> Unit,
) {
    var dialogVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PrimaryButton(onClick = { dialogVisible = true }) {
            Label("New project", fontSize = 30.sp)
        }
        Label("or", fontSize = 30.sp)
        PrimaryButton(onClick = { onLoadProject() }) {
            Label("Load project", fontSize = 30.sp)
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
            },
            filterList = "json"
        )
    }
}
