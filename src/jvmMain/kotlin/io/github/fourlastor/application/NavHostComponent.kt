package io.github.fourlastor.application

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import io.github.fourlastor.editor.EditorComponent

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
    ): Component {
        return when (screenConfig) {

            is ScreenConfig.New -> EditorComponent(
                componentContext
            )

            is ScreenConfig.Project -> TODO()
        }
    }


    @Composable
    override fun content() {
        Children(
            stack = stack,
        ) {
            it.instance.content()
        }
    }

    private sealed class ScreenConfig : Parcelable {
        object New : ScreenConfig()
        data class Project(val name: String) : ScreenConfig()
    }
}
