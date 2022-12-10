package io.github.fourlastor.file

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import io.github.fourlastor.application.Component
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path.Companion.toPath

class FileDialogComponent(
    context: ComponentContext,
    initialDirectory: String? = null
) : Component, ComponentContext by context {
    private val path = initialDirectory?.toPath() ?: System.getProperty("user.home").toPath()
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val fileSystem = FileSystem.SYSTEM

    private val state = MutableStateFlow<FileDialogState>(FileDialogState.Loading)

    init {
        lifecycle.doOnCreate {
            scope.launch(Dispatchers.IO) {
                val newState = fileSystem.list(path)
                    .asSequence()
                    .map { it to fileSystem.metadata(it) }
                    .filter { (path) -> !path.name.startsWith(".") }
                    .map { (path, metadata) ->
                        if (metadata.isDirectory) {
                            FileDialogEntry.Folder(path.name)
                        } else {
                            FileDialogEntry.File(path.name)
                        }
                    }
                    .sortedBy { it.name }
                    .toImmutableList()
                    .let { FileDialogState.Loaded(it) }
                state.update { newState }
            }
        }
        lifecycle.doOnDestroy {
            scope.cancel()
        }
    }

    @Composable
    override fun render() {
        val currentState = state.value
        if (currentState is FileDialogState.Loaded) {
            NewFileDialog(
                state = currentState,
            ) {

            }
        }
    }
}
