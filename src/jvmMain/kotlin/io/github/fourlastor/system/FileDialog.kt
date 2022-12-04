package io.github.fourlastor.system

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun FileLoadDialog(
    parent: Frame? = null,
    onCloseRequest: (result: File?) -> Unit,
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Choose a file", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(files.getOrNull(0))
                }
            }
        }
    },
    dispose = FileDialog::dispose
)

@Composable
fun FileSaveDialog(
    parent: Frame? = null,
    onCloseRequest: (result: File?) -> Unit,
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Choose a file", SAVE) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(files.getOrNull(0))
                }
            }
        }
    },
    dispose = FileDialog::dispose
)
