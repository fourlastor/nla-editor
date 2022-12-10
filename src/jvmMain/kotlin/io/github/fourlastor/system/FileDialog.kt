package io.github.fourlastor.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skiko.MainUIDispatcher
import java.awt.Frame
import java.io.File
import javax.swing.JFileChooser

@Composable
fun FileLoadDialog(
    parent: Frame? = null,
    onCloseRequest: (result: File?) -> Unit,
    config: JFileChooser.() -> Unit = {},
) {
    FileDialog(config, onCloseRequest) { it.showOpenDialog(parent) }
}

@Composable
fun FileSaveDialog(
    parent: Frame? = null,
    onCloseRequest: (result: File?) -> Unit,
    config: JFileChooser.() -> Unit = {},
) {
    FileDialog(config, onCloseRequest) { it.showSaveDialog(parent) }
}

@Composable
private fun FileDialog(
    config: JFileChooser.() -> Unit,
    onCloseRequest: (result: File?) -> Unit,
    action: (JFileChooser) -> Int,
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) {
        val job = scope.launch {
            val chooser = JFileChooser().apply(config)
            val result = withContext(MainUIDispatcher) {
                action(chooser)
            }
            val file = when (result) {
                JFileChooser.APPROVE_OPTION -> {
                    chooser.selectedFile
                }

                else -> null
            }
            onCloseRequest(file)
        }

        onDispose {
            job.cancel()
        }
    }
}
