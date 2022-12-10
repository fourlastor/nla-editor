package io.github.fourlastor.newProject

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import io.github.fourlastor.application.Component

class NewProjectComponent(
    private val context: ComponentContext,
    private val onNewProject: (path: String) -> Unit,
    private val onLoadProject: () -> Unit,
) : Component, ComponentContext by context {

    @Composable
    override fun render() {
        NewProject(onNewProject = onNewProject, onLoadProject = onLoadProject)
    }
}
