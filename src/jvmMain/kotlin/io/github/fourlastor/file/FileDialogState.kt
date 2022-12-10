package io.github.fourlastor.file

import kotlinx.collections.immutable.ImmutableList

sealed class FileDialogState {
    object Loading : FileDialogState()
    data class Loaded(
        val files: ImmutableList<FileDialogEntry>
    ) : FileDialogState()
}

sealed class FileDialogEntry(
    val name: String,
) {
    class File(name: String) : FileDialogEntry(name)
    class Folder(name: String) : FileDialogEntry(name)
}
