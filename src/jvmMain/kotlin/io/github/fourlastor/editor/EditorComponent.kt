package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import io.github.fourlastor.application.Component
import io.github.fourlastor.data.LoadableProject
import io.github.fourlastor.data.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import okio.FileSystem
import kotlin.time.Duration

class EditorComponent(
    componentContext: ComponentContext,
    path: String,
) : Component, ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val viewModel = ViewModel(
        scope = scope,
        fileSystem = FileSystem.SYSTEM,
    )

    /** Local state, it's used to display or not the save popup. */
    private var saveRequested by mutableStateOf(false)

    init {
        lifecycle.doOnCreate {
            viewModel.load(path)
        }

        lifecycle.doOnDestroy {
            scope.cancel()
        }
    }

    @Composable
    override fun render() {
        val project by viewModel.project.collectAsState(LoadableProject.Loading)
        AnimationEditor(
            loadable = project,
            entityUpdater = { id, update -> viewModel.updateEntity(id, update) },
            saveRequested = saveRequested,
            onAddGroup = { viewModel.group(it, "Group") },
            onDeleteEntity = { viewModel.deleteEntity(it) },
            onAddAnimation = { name, duration -> viewModel.animation(name, duration) },
            onAddImage = { parentId: Long, name: String, path: String -> viewModel.image(parentId, name, path) },
            onAddKeyFrame = { animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration ->
                viewModel.keyFrame(animationId, entityId, propertyId, position, value)
            },
            onUpdateAnimation = { id, update -> viewModel.updateAnimation(id, update) }
        ) { saveRequested = false }
    }
}
