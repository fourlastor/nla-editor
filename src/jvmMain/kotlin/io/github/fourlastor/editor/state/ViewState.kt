package io.github.fourlastor.editor.state

data class ViewState(
    val animations: AnimationState,
) {
    sealed class AnimationState

    object Disabled : AnimationState()
    sealed class Enabled : AnimationState()

    object Selecting : Enabled()
    class Selected(val id: Long) : Enabled()


    companion object {
        fun initial() = ViewState(
            animations = Disabled,
        )
    }
}
