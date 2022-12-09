package io.github.fourlastor.load

import androidx.compose.runtime.Composable
import io.github.fourlastor.system.FileLoadDialog
import io.kanro.compose.jetbrains.expui.window.LocalWindow

@Composable
fun LoadProject(
    onSuccess: (path: String) -> Unit,
    onCancel: () -> Unit,
) {
    FileLoadDialog(parent = LocalWindow.current) {
        if (it != null) {
            onSuccess(it.absolutePath)
        } else {
            onCancel()
        }
    }
}
