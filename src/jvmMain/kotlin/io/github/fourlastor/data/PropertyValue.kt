package io.github.fourlastor.data

import kotlinx.serialization.Serializable

@Serializable
data class PropertyValue(
    val id: Long,
    val value: Float
)
