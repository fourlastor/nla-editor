package io.github.fourlastor.application

import androidx.compose.runtime.Composable

interface Component {
    @Composable
    fun toolbar()

    @Composable
    fun content()
}
