package io.github.fourlastor.editor.save

import androidx.compose.runtime.Composable
import io.github.fourlastor.data.VersionedProject
import io.github.fourlastor.system.FileLoadDialog
import kotlinx.serialization.json.Json

@Composable
fun LoadProject(
    onSuccess: (project: VersionedProject.V1) -> Unit,
    onFailure: (cause: Throwable) -> Unit,
    onCancel: () -> Unit,
) {
    FileLoadDialog {
        if (it != null) {
            val result = runCatching {
                Json.decodeFromString(
                    VersionedProject.serializer(),
                    it.readText()
                ).migrateToLatest()
            }

            result.onSuccess(onSuccess)
            result.onFailure(onFailure)
        }
        onCancel()
    }
}

/** This will be useful in the future, to version the projects. */
private fun VersionedProject.migrateToLatest(): VersionedProject.V1 = when (this) {
    is VersionedProject.V1 -> this
}
