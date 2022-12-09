package io.github.fourlastor.load

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import io.github.fourlastor.application.Component

class LoadComponent(
    private val onLoad: (path: String) -> Unit,
    private val onCancel: () -> Unit,
    context: ComponentContext,
) : Component, ComponentContext by context {
    @Composable
    override fun toolbar() {
    }

    @Composable
    override fun content() {
        LoadProject(
            onSuccess = onLoad,
            onCancel = onCancel
        )
    }
}
