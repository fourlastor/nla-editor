package io.github.fourlastor.data

sealed interface LoadableProject {
    object Loading : LoadableProject
    data class Loaded(
        val result: LatestProject,
    ) : LoadableProject
}
