package io.github.fourlastor.editor.save

import androidx.compose.runtime.Composable
import io.github.fourlastor.data.LatestProject
import io.github.fourlastor.data.Project
import io.github.fourlastor.system.FileLoadDialog
import kotlinx.serialization.json.Json

@Composable
fun LoadProject(
    onSuccess: (project: LatestProject) -> Unit,
    onFailure: (cause: Throwable) -> Unit,
    onCancel: () -> Unit,
) {
    FileLoadDialog {
        if (it != null) {
            val result = runCatching {
                Json.decodeFromString(
                    Project.serializer(),
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
private fun Project.migrateToLatest(): LatestProject = when (this) {
    is Project.V1 -> this
}
