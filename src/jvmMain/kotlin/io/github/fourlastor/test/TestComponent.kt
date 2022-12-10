package io.github.fourlastor.test

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import io.github.fourlastor.application.Component

class TestComponent(
    context: ComponentContext,
) : Component, ComponentContext by context {

    @Composable
    override fun render() {
        TestApp()
    }
}
