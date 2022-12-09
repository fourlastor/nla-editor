package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import io.github.fourlastor.system.FileLoadDialog
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun AddImage(
    parentId: Long?,
    projectPath: String,
    onAddImage: (parentId: Long, name: String, path: String) -> Unit,
    onCancel: () -> Unit,
) {
    if (parentId == null) {
        return
    }

    FileLoadDialog(
        onCloseRequest = {
            if (it != null) {
                onAddImage(parentId, "Image", it.absolutePath)
            } else {
                onCancel()
            }
        },
        config = {
            fileFilter = FileNameExtensionFilter("Images", "png", "jpeg", "jpg")
            currentDirectory = File(projectPath)
        }
    )
}
