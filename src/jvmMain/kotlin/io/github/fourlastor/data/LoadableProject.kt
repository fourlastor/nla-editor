package io.github.fourlastor.data

sealed interface LoadableProject {
    object Loading : LoadableProject
    data class Loaded(
        val entities: Entities,
        val animations: Animations,
    ) : LoadableProject
}
