package io.github.fourlastor.newProject

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import io.github.fourlastor.application.Component

class NewProjectComponent(
    private val context: ComponentContext,
    private val onNewProject: (path: String) -> Unit,
) : Component, ComponentContext by context {

    @Composable
    override fun toolbar() {
    }

    @Composable
    override fun content() {
        NewProject(onNewProject = onNewProject)
    }
}
