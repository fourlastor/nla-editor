package io.github.fourlastor.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.unit.dp
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Timeline(
    modifier: Modifier,
    duration: Duration = 5.seconds,
) {
    var zoom by remember { mutableStateOf(1f) }
    val secondWidth = 300.dp * zoom
    val horizontalScrollState = rememberScrollState(0)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
//        Slider(value = zoom, modifier = Modifier.fillMaxWidth(), onValueChange = { zoom = it })

        HorizontalScrollbar(
            modifier = Modifier
                .fillMaxWidth(),
            adapter = rememberScrollbarAdapter(horizontalScrollState)
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .horizontalScroll(horizontalScrollState)
                .onDrag(matcher = PointerMatcher.mouse(PointerButton.Tertiary)) {
                    coroutineScope.launch {
                        horizontalScrollState.scrollTo((horizontalScrollState.value - it.x).toInt())
                    }
                }
        ) {
            Row {
                repeat(duration.inWholeSeconds.toInt()) { counter ->
                    Box(
                        modifier = Modifier.width(secondWidth)
                            .height(40.dp)
                            .padding(2.dp)
                    ) {
                        Label(counter.toString())
                    }
                }
            }
            val color = LocalAreaColors.current.text
            Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                val secondOffset = secondWidth.toPx()
                for (s in 0 until duration.inWholeSeconds) {
                    val xOffset = s * secondOffset
                    drawLine(
                        color = color,
                        start = Offset(xOffset, 0f),
                        end = Offset(xOffset, 150f),
                        strokeWidth = 2f,
                    )

                    for (ms in 1 until 10) {
                        val msOffset = ms / 10f * secondOffset
                        drawLine(
                            color = color,
                            start = Offset(xOffset + msOffset, 0f),
                            end = Offset(xOffset + msOffset, 50f),
                            strokeWidth = 1f,
                        )
                    }
                }
            }
        }
    }

}
