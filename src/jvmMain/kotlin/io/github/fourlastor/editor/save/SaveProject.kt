package io.github.fourlastor.editor.save

import androidx.compose.runtime.Composable
import io.github.fourlastor.data.Entities
import io.github.fourlastor.system.FileSaveDialog
import kotlinx.serialization.json.Json

@Composable
fun SaveProject(
    entities: Entities,
    onSuccess: () -> Unit,
    onFailure: (cause: Throwable) -> Unit,
    onCancel: () -> Unit,
) {
    FileSaveDialog {
        if (it != null) {
            val result = runCatching {
                it.writeText(Json.encodeToString(Entities.serializer(), entities))
            }

            result.onSuccess { onSuccess() }
            result.onFailure(onFailure)
        }
        onCancel()
    }
}
