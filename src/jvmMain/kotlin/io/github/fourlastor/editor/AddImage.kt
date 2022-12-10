package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import io.github.fourlastor.system.FileLoadDialog
import okio.Path
import okio.Path.Companion.toOkioPath
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun AddImage(
    parentId: Long?,
    projectPath: Path,
    onAddImage: (parentId: Long, name: String, path: String) -> Unit,
    onCancel: () -> Unit,
) {
    if (parentId == null) {
        return
    }

    FileLoadDialog(
        onCloseRequest = {
            if (it != null) {
                onAddImage(parentId, "Image", it.toOkioPath().relativeTo(projectPath).toFile().absolutePath)
            } else {
                onCancel()
            }
        },
        config = {
            fileFilter = FileNameExtensionFilter("Images", "png", "jpeg", "jpg")
            currentDirectory = projectPath.toFile()
        }
    )
}
