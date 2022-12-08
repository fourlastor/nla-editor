package io.github.fourlastor.editor.state

import kotlin.time.Duration

data class ViewState(
    val animations: AnimationState,
) {
    sealed class AnimationState

    object Disabled : AnimationState()
    sealed class Enabled : AnimationState()

    object Selecting : Enabled()
    data class Selected(
        val id: Long,
        val trackPosition: Duration = Duration.ZERO
    ) : Enabled()


    companion object {
        fun initial() = ViewState(
            animations = Disabled,
        )
    }
}
