package io.github.fourlastor.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import io.kanro.compose.jetbrains.expui.style.areaBackground
import io.kanro.compose.jetbrains.expui.window.MainToolBarScope

@Composable
fun MainToolBarScope.EditorToolbar() {
    Box(
        modifier = Modifier
            .mainToolBarItem(alignment = Alignment.End)
            .fillMaxSize()
            .zIndex(2f)
            .areaBackground()
    )
}
