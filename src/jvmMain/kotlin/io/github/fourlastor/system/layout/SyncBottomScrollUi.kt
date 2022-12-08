package io.github.fourlastor.system.layout

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

/** 2x2 grid that can be resized by dragging the separators. */
@Composable
fun SyncBottomScrollUi(
    modifier: Modifier,
    topLeft: @Composable BoxScope.() -> Unit,
    topRight: @Composable BoxScope.() -> Unit,
    bottomLeft: @Composable BoxScope.(state: LazyListState) -> Unit,
    bottomRight: @Composable BoxScope.(state: LazyListState) -> Unit,
) {
    val bottomLeftState = rememberLazyListState()
    val bottomRightState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollableState { delta ->
        scope.launch {
            bottomLeftState.scrollBy(-delta)
            bottomRightState.scrollBy(-delta)
        }
        delta
    }
    GridUi(
        modifier = modifier,
        topLeft = topLeft,
        topRight = topRight,
        bottomLeft = {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scrollable(scrollState, Orientation.Vertical)
            ) {
                bottomLeft(bottomLeftState)
            }
        },
        bottomRight = {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scrollable(scrollState, Orientation.Vertical)
            ) {
                bottomRight(bottomRightState)
            }
        }
    )
}
