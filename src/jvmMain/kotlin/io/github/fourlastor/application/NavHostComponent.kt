package io.github.fourlastor.application

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.essenty.parcelable.Parcelable
import io.github.fourlastor.editor.EditorComponent
import io.github.fourlastor.load.LoadComponent
import io.github.fourlastor.newProject.NewProjectComponent
import io.github.fourlastor.toolbar.Toolbar
import io.github.fourlastor.toolbar.ToolbarButton

@OptIn(ExperimentalDecomposeApi::class)
class NavHostComponent(
    componentContext: ComponentContext
) : Component, ComponentContext by componentContext {
    private val navigation = StackNavigation<ScreenConfig>()
    private val stack = childStack(
        source = navigation,
        initialConfiguration = ScreenConfig.New,
        childFactory = ::createScreenComponent
    )

    /**
     * Factory function to create screen from given ScreenConfig
     */
    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ): Component = when (screenConfig) {

        is ScreenConfig.New -> NewProjectComponent(
            context = componentContext,
            onNewProject = ::openProject
        )

        is ScreenConfig.Project -> EditorComponent(
            componentContext,
            path = screenConfig.name
        )

        is ScreenConfig.Load -> LoadComponent(
            onLoad = ::openProject,
            onCancel = { navigation.pop() },
            context = componentContext
        )
    }

    fun loadProject() {
        navigation.push(ScreenConfig.Load)
    }

    private fun openProject(path: String) {
        navigation.replaceAll(ScreenConfig.Project(path))
    }

    @Composable
    override fun toolbar() {
        Children(
            stack = stack,
        ) {
            Toolbar {
                ToolbarButton("Load", "icons/load.svg", onClick = ::loadProject)
                it.instance.toolbar()
            }
        }
    }

    @Composable
    override fun render() {
        Children(
            stack = stack,
        ) {
            it.instance.render()
        }
    }

    private sealed class ScreenConfig : Parcelable {
        object New : ScreenConfig()
        data class Project(val name: String) : ScreenConfig()
        object Load : ScreenConfig()
    }
}
