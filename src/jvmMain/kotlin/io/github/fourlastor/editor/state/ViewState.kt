package io.github.fourlastor.editor.state

data class ViewState(
    val animationsEnabled: Boolean,
) {
    companion object {
        fun initial() = ViewState(
            animationsEnabled = false,
        )
    }
}
