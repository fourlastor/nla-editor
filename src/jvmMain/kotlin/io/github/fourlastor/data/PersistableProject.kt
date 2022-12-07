package io.github.fourlastor.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface PersistableProject {
    @Serializable
    data class V1(
        val entities: Entities,
        val animations: Animations,
        val lastEntityId: Long,
        val lastPropertyId: Long,
        val lastAnimationId: Long,
    ) : PersistableProject {
    }
}

typealias LatestProject = PersistableProject.V1
