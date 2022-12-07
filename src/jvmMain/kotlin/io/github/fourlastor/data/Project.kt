package io.github.fourlastor.data

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed interface Project {

    @Serializable
    data class V1(
        val entities: Entities,
        val animations: Animations,
    ) : Project {
        fun animation(name: String, duration: Duration): V1 = copy(animations = animations.animation(name, duration))
    }
}

typealias LatestProject = Project.V1
