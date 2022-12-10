package io.github.fourlastor.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.lwjgl.system.MemoryUtil.memAllocPointer
import org.lwjgl.system.MemoryUtil.memFree
import org.lwjgl.util.nfd.NativeFileDialog
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
            var initialPath = System.getProperty("user.home")

            if (System.getProperty("os.name").lowercase().contains("win")) {
                initialPath = initialPath.replace("/", "\\")
            }

            val pathPointer = memAllocPointer(1);

            try {
                val status = NativeFileDialog.NFD_PickFolder(initialPath, pathPointer)

                if (status == NativeFileDialog.NFD_CANCEL) {
                    onCloseRequest(null)
                    return@launch
                }

                // unexpected error -> show visui dialog
                if (status != NativeFileDialog.NFD_OKAY) {
                    println("Error with native dialog")
                    onCloseRequest(null)
                    return@launch
                }

                val folder = pathPointer.getStringUTF8(0)
                NativeFileDialog.nNFD_Free(pathPointer.get(0))

                println("Selected $folder")
            } catch (e: Throwable) {
                // TODO
            } finally {
                memFree(pathPointer)
            }
        }

        onDispose {
            job.cancel()
        }
    }
}
