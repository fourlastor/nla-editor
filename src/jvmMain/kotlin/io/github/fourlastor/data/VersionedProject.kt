package io.github.fourlastor.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface VersionedProject {

    @Serializable
    data class V1(
        val entities: Entities,
    ) : VersionedProject
}
