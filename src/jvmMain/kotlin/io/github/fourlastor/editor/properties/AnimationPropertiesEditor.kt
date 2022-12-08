package io.github.fourlastor.editor.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import io.github.fourlastor.data.Animations
import io.github.fourlastor.editor.state.ViewState
import io.kanro.compose.jetbrains.expui.control.ComboBox
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.OutlineButton
import io.kanro.compose.jetbrains.expui.control.TextField
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import io.github.fourlastor.data.Animation as DataAnimation

private typealias AnimationUpdate = (animation: DataAnimation) -> DataAnimation

@Composable
fun AnimationPropertiesEditor(
    viewState: ViewState.Enabled,
    animations: Animations,
    modifier: Modifier = Modifier,
    onSelectAnimation: (animationId: Long) -> Unit,
    onUpdateAnimation: (animationId: Long, animationUpdate: AnimationUpdate) -> Unit,
    onAddAnimation: (name: String, duration: Duration) -> Unit,
    onSeek: (animationId: Long, position: Duration) -> Unit,
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .background(DarkTheme.Grey1)
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .padding(horizontal = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            val state by remember(animations, viewState) { derivedStateOf { animations.toState(viewState) } }
            ComboBox(
                items = state.animations,
                value = state.selectedAnimation,
                valueRender = { Label(it.name) },
                onValueChange = { onSelectAnimation(it.id) },
                modifier = Modifier.width(150.dp),
                menuModifier = Modifier.width(150.dp)
            )
            val selectedAnimation = state.selectedAnimation
            val duration = selectedAnimation.duration
            val position = state.position

            AnimationPropertyField(
                value = selectedAnimation.name,
                validator = { it },
                onUpdate = { name -> onUpdateAnimation(selectedAnimation.id) { it.copy(name = name) } }
            )
            AnimationPropertyField(
                value = "${position.inWholeMilliseconds}",
                validator = { it.toLongOrNull()?.milliseconds },
                onUpdate = {
                    onSeek(state.selectedAnimation.id, it)
                }
            )
            AnimationPropertyField(
                value = duration.inWholeMilliseconds.toString(),
                validator = { (it.toLongOrNull() ?: 0L).milliseconds },
                onUpdate = { newDuration -> onUpdateAnimation(selectedAnimation.id) { it.copy(duration = newDuration) } }
            )
            OutlineButton(
                onClick = {
                    onAddAnimation("New animation", 5.seconds)
                },
            ) {
                Label("New")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun <T> AnimationPropertyField(
    value: String,
    validator: (String) -> T?,
    onUpdate: (T) -> Unit,
) {
    var currentValue by remember(value) { mutableStateOf(value) }
    val isDirty by remember(value, currentValue) { derivedStateOf { value != currentValue } }
    TextField(
        value = currentValue,
        onValueChange = { currentValue = it },
        modifier = Modifier
            .onFocusChanged {
                if (!it.hasFocus && isDirty) {
                    validator(currentValue)
                        ?.also(onUpdate)
                }
            }
            .onKeyEvent { event ->
                if (isDirty && event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                    val validated = validator(currentValue)
                    validated
                        ?.also(onUpdate)
                        ?.let { true }
                        ?: false
                } else {
                    false
                }
            }
    )
}

private fun Animations.toState(animationState: ViewState.Enabled): AnimationPropertiesState {
    val selectedAnimation: AnimationInEditor = if (animationState is ViewState.Selected) {
        this[animationState.id].toAnimation()
    } else {
        None
    }
    val position: Duration = if (animationState is ViewState.Selected) {
        animationState.trackPosition
    } else {
        Duration.ZERO
    }
    return AnimationPropertiesState(
        selectedAnimation = selectedAnimation,
        position = position,
        animations = animations.values.map { it.toAnimation() }.toImmutableList(),
    )
}

private fun DataAnimation.toAnimation(): Animation {
    return Animation(id, name, duration)
}

private data class AnimationPropertiesState(
    val selectedAnimation: AnimationInEditor,
    val position: Duration,
    val animations: ImmutableList<Animation>,
)

private sealed class AnimationInEditor(
    val id: Long,
    val name: String,
    val duration: Duration,
)

private object None : AnimationInEditor(-1, "", Duration.ZERO)
private open class Animation(
    id: Long,
    name: String,
    duration: Duration,
) : AnimationInEditor(id, name, duration)
