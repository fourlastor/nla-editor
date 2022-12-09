package io.github.fourlastor.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import io.github.fourlastor.application.Component
import io.github.fourlastor.data.LoadableProject
import io.github.fourlastor.data.ViewModel
import kotlin.time.Duration

class EditorComponent(
    componentContext: ComponentContext,
    private val onLoad: () -> Unit,
) : Component, ComponentContext by componentContext {

    private val viewModel = ViewModel()

    /** Local state, it's used to display or not the save popup. */
    private var saveRequested by mutableStateOf(false)

    @Composable
    override fun toolbar() {
        EditorToolbar(
            onLoad = onLoad,
            onSave = { saveRequested = true }
        )
    }

    @Composable
    override fun content() {
        val project by viewModel.project.collectAsState(LoadableProject.Loading)
        AnimationEditor(
            loadable = project,
            saveRequested = saveRequested,
            entityUpdater = { id, update -> viewModel.updateEntity(id, update) },
            onAddGroup = { viewModel.group(it, "Group") },
            onDeleteEntity = { viewModel.deleteEntity(it) },
            onAddAnimation = { name, duration -> viewModel.animation(name, duration) },
            onLoadProject = { viewModel.load(it) },
            onAddImage = { parentId: Long, name: String, path: String -> viewModel.image(parentId, name, path) },
            onAddKeyFrame = { animationId: Long, entityId: Long, propertyId: Long, value: Float, position: Duration ->
                viewModel.keyFrame(animationId, entityId, propertyId, position, value)
            },
            onUpdateAnimation = { id, update -> viewModel.updateAnimation(id, update) }
        ) { saveRequested = false }
    }
}
