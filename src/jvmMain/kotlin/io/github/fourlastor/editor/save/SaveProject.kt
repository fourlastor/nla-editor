package io.github.fourlastor.editor.save

import androidx.compose.runtime.Composable
import io.github.fourlastor.data.VersionedProject
import io.github.fourlastor.system.FileSaveDialog
import kotlinx.serialization.json.Json

@Composable
fun SaveProject(
    project: VersionedProject,
    onSuccess: () -> Unit,
    onFailure: (cause: Throwable) -> Unit,
    onCancel: () -> Unit,
) {
    FileSaveDialog {
        if (it != null) {
            val result = runCatching {
                it.writeText(Json.encodeToString(VersionedProject.serializer(), project))
            }

            result.onSuccess { onSuccess() }
            result.onFailure(onFailure)
        }
        onCancel()
    }
}
