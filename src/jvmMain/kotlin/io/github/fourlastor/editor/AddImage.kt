package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import io.github.fourlastor.system.FileLoadDialog
import okio.Path
import okio.Path.Companion.toOkioPath

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
                onAddImage(parentId, "Image", it.toOkioPath().relativeTo(projectPath).normalized().toString())
            } else {
                onCancel()
            }
        },
        filterList = "png,jpeg,jpg",
        initialPath = projectPath.toString(),
    )
}
