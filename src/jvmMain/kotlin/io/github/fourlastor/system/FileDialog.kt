package io.github.fourlastor.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.AwtWindow
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun FileLoadDialog(
    parent: Frame? = null,
    onCloseRequest: (result: File?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    AwtWindow(
        create = {
            object : FileDialog(parent, "Choose a file", LOAD) {
                override fun setVisible(value: Boolean) {
                    super.setVisible(value)
                    if (!value) {
                        scope.launch {
                            onCloseRequest(files.getOrNull(0))
                        }
                    }
                }
            }
        },
        dispose = FileDialog::dispose
    )
}

@Composable
fun FileSaveDialog(
    parent: Frame? = null,
    onCloseRequest: (result: File?) -> Unit,
) {
    val scope = rememberCoroutineScope()

    AwtWindow(
        create = {
            object : FileDialog(parent, "Choose a file", SAVE) {
                override fun setVisible(value: Boolean) {
                    super.setVisible(value)
                    if (!value) {
                        scope.launch {
                            onCloseRequest(files.getOrNull(0))
                        }
                    }
                }
            }
        },
        dispose = FileDialog::dispose
    )
}
