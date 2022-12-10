package io.github.fourlastor.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.lwjgl.system.MemoryUtil.memAllocPointer
import org.lwjgl.system.MemoryUtil.memFree
import org.lwjgl.util.nfd.NativeFileDialog
import java.io.File

@Composable
fun FileLoadDialog(
    onCloseRequest: (result: File?) -> Unit,
    filterList: String,
    initialPath: String? = null,
) {
    FileDialog(
        type = Type.Load,
        filterList = filterList,
        onCloseRequest = onCloseRequest,
        initialPath = initialPath,
    )
}

@Composable
fun FileSaveDialog(
    onCloseRequest: (result: File?) -> Unit,
    filterList: String? = null,
    initialPath: String? = null,
) {
    FileDialog(
        type = Type.Save,
        filterList = filterList,
        onCloseRequest = onCloseRequest,
        initialPath = initialPath,
    )
}

@Composable
private fun FileDialog(
    type: Type,
    initialPath: String?,
    filterList: String? = null,
    onCloseRequest: (result: File?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) {
        val job = scope.launch {
            val path = (initialPath ?: System.getProperty("user.home")).let {
                if (System.getProperty("os.name").lowercase().contains("win")) {
                    it.replace("/", "\\")
                } else {
                    it
                }
            }

            val pathPointer = memAllocPointer(1)

            try {
                val status = when (type) {
                    Type.Load -> NativeFileDialog.NFD_OpenDialog(filterList, path, pathPointer)
                    Type.Save -> NativeFileDialog.NFD_SaveDialog(filterList, path, pathPointer)
                }


                if (status == NativeFileDialog.NFD_CANCEL) {
                    onCloseRequest(null)
                    return@launch
                }

                if (status != NativeFileDialog.NFD_OKAY) {
                    println("Error with native dialog")
                    onCloseRequest(null)
                    return@launch
                }

                val result = pathPointer.getStringUTF8(0)
                NativeFileDialog.nNFD_Free(pathPointer.get(0))
                onCloseRequest(File(result))
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

private enum class Type {
    Load, Save
}
