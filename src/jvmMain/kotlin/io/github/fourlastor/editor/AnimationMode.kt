package io.github.fourlastor.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.fourlastor.data.Animation
import io.github.fourlastor.data.Animations
import io.github.fourlastor.data.LatestProject
import io.github.fourlastor.data.Project
import io.github.fourlastor.editor.state.ViewState
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.ComboBox
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.SegmentedButton
import io.kanro.compose.jetbrains.expui.control.TextField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun AnimationMode(
    project: LatestProject,
    viewState: ViewState,
    onAnimationToggle: (enabled: Boolean) -> Unit,
    onAnimationSelected: (id: Long) -> Unit,
    onCreateAnimation: (name: String, duration: Duration) -> Unit,
) {
    val state by rememberAnimationModeState(viewState, project)
    AnimationModeUi(
        state = state,
        onAnimationToggle = onAnimationToggle,
        onAnimationSelected = onAnimationSelected,
        onCreateAnimation = onCreateAnimation
    )
}

@Composable
private fun AnimationModeUi(
    state: AnimationModeState,
    onAnimationToggle: (enabled: Boolean) -> Unit,
    onAnimationSelected: (id: Long) -> Unit,
    onCreateAnimation: (name: String, duration: Duration) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(40.dp).padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Label("Animation mode: ")
        val selectedIndex by remember(state) { derivedStateOf { if (state is AnimationModeState.Enabled) 1 else 0 } }
        SegmentedButton(2, selectedIndex, {
            onAnimationToggle(it != 0)
        }) {
            when (it) {
                0 -> Label("Disabled")
                1 -> Label("Enabled")
                else -> error("Invalid index")
            }
        }
        if (state is AnimationModeState.Enabled) {
            Label("Animation: ")
            val missingAnimation = remember { AnimationModeAnimation(-1, "") }
            val selectedAnimation by remember(state) {
                derivedStateOf {
                    if (state is AnimationModeState.Selected) {
                        state.selectedAnimation
                    } else missingAnimation
                }
            }

            ComboBox(
                items = state.animations,
                value = selectedAnimation,
                valueRender = { Label(it.name) },
                onValueChange = { onAnimationSelected(it.id) },
                modifier = Modifier.width(150.dp),
                menuModifier = Modifier.width(150.dp)
            )
            Label("New animation")
            var animationName by remember { mutableStateOf("") }
            TextField(animationName, { animationName = it })
            ActionButton(
                onClick = {
                    val trimmed = animationName.trim()
                    if (trimmed.isEmpty()) {
                        return@ActionButton
                    }
                    onCreateAnimation(trimmed, 5.seconds)
                    animationName = ""
                }
            ) {
                Label("Add")
            }
        }
    }
}

sealed class AnimationModeState {
    object Disabled : AnimationModeState()
    sealed class Enabled(val animations: ImmutableList<AnimationModeAnimation>) : AnimationModeState()
    class Selecting(
        animations: ImmutableList<AnimationModeAnimation>,
    ) : Enabled(animations)

    class Selected(
        val selectedAnimation: AnimationModeAnimation,
        animations: ImmutableList<AnimationModeAnimation>,
    ) : Enabled(animations)
}

data class AnimationModeAnimation(
    val id: Long,
    val name: String,
) {

}

@Composable
private fun rememberAnimationModeState(
    viewState: ViewState,
    project: Project.V1,
): State<AnimationModeState> = remember(viewState.animations, project.animations) {
    derivedStateOf {
        when (val animations = viewState.animations) {
            is ViewState.Disabled -> AnimationModeState.Disabled
            is ViewState.Selecting -> AnimationModeState.Selecting(
                animations = project.animations.toAnimations()
            )

            is ViewState.Selected -> AnimationModeState.Selected(
                selectedAnimation = project.animations.byId(animations.id).toAnimation(),
                animations = project.animations.toAnimations()
            )
        }
    }
}

private fun Animations.toAnimations() = animations.map { (_, it) -> it.toAnimation() }.toImmutableList()

private fun Animation.toAnimation(): AnimationModeAnimation = AnimationModeAnimation(
    id, name
)
