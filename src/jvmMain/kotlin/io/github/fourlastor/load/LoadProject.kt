package io.github.fourlastor.load

import androidx.compose.runtime.Composable
import io.github.fourlastor.data.LatestProject
import io.github.fourlastor.data.PersistableProject
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

/** This will be useful in the future, to version the projects. */
private fun PersistableProject.migrateToLatest(): LatestProject = when (this) {
    is PersistableProject.V1 -> this
}
