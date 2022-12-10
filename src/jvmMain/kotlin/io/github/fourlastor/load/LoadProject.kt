package io.github.fourlastor.load

import androidx.compose.runtime.Composable
import io.github.fourlastor.system.FileLoadDialog

@Composable
fun LoadProject(
    onSuccess: (path: String) -> Unit,
    onCancel: () -> Unit,
) {
    FileLoadDialog(
        onCloseRequest = {
            if (it != null) {
                onSuccess(it.absolutePath)
            } else {
                onCancel()
            }
        },
        filterList = "json"
    )
}
