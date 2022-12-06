package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import io.github.fourlastor.system.FileLoadDialog

@Composable
fun AddImage(
    parentId: Long?,
    onAddImage: (parentId: Long, name: String, path: String) -> Unit,
    onCancel: () -> Unit,
) {
    if (parentId == null) {
        return
    }

    FileLoadDialog {
        if (it != null) {
            onAddImage(parentId, "Image", it.absolutePath)
        } else {
            onCancel()
        }
    }
}
