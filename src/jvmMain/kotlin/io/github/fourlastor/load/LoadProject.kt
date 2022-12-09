package io.github.fourlastor.load

import androidx.compose.runtime.Composable
import io.github.fourlastor.system.FileLoadDialog
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun LoadProject(
    onSuccess: (path: String) -> Unit,
    onCancel: () -> Unit,
) {
    FileLoadDialog(onCloseRequest = {
        if (it != null) {
            onSuccess(it.absolutePath)
        } else {
            onCancel()
        }
    }, config = {
        fileFilter = FileNameExtensionFilter("Project file", "json")
    })
}
