package io.github.fourlastor.data

import okio.Path

sealed interface LoadableProject {
    object Loading : LoadableProject
    data class Loaded(
        val entities: Entities,
        val animations: Animations,
        val path: Path,
    ) : LoadableProject
}
