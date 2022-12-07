package io.github.fourlastor.editor.save

import androidx.compose.runtime.Composable
import io.github.fourlastor.data.Entities
import io.github.fourlastor.system.FileLoadDialog
import kotlinx.serialization.json.Json

@Composable
fun LoadProject(
    onSuccess: (entities: Entities) -> Unit,
    onFailure: (cause: Throwable) -> Unit,
    onCancel: () -> Unit,
) {
    FileLoadDialog {
        if (it != null) {
            val result = runCatching {
                Json.decodeFromString(
                    Entities.serializer(),
                    it.readText()
                )
            }

            result.onSuccess(onSuccess)
            result.onFailure(onFailure)
        }
        onCancel()
    }
}
