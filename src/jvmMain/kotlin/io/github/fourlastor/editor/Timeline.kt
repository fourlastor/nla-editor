package io.github.fourlastor.editor

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.dp
import io.github.fourlastor.entity.Entities
import io.kanro.compose.jetbrains.expui.control.Icon
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Timeline(
    duration: Duration = 5.seconds,
    entities: Entities,
    propertyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    var zoom by remember { mutableStateOf(1f) }
    val secondWidth = 300.dp * zoom
    val horizontalScrollState = rememberScrollState(0)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
//        Slider(value = zoom, modifier = Modifier.fillMaxWidth(), onValueChange = { zoom = it })

        HorizontalScrollbar(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            adapter = rememberScrollbarAdapter(horizontalScrollState)
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
                .onDrag(matcher = PointerMatcher.mouse(PointerButton.Tertiary)) {
                    coroutineScope.launch {
                        horizontalScrollState.scrollTo((horizontalScrollState.value - it.x).toInt())
                    }
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box {
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
                    Canvas(modifier = Modifier.fillMaxWidth().height(50.dp)) {
                        val secondOffset = secondWidth.toPx()
                        for (s in 0 until duration.inWholeSeconds) {
                            val xOffset = s * secondOffset
                            drawLine(
                                color = color,
                                start = Offset(xOffset, 0f),
                                end = Offset(xOffset, 50f),
                                strokeWidth = 2f,
                            )

                            for (ms in 1 until 10) {
                                val msOffset = ms / 10f * secondOffset
                                drawLine(
                                    color = color,
                                    start = Offset(xOffset + msOffset, 0f),
                                    end = Offset(xOffset + msOffset, 15f),
                                    strokeWidth = 1f,
                                )
                            }
                        }
                    }
                }
                val trackWidth = secondWidth * duration.inWholeMilliseconds.toInt() / 1000
                LazyColumn(
                    modifier = Modifier.width(trackWidth)
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    state = propertyListState,
                    userScrollEnabled = false,
                ) {
                    items(
                        count = entities.entities.size,
                        key = { entities.entities[it].id }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Spacer(
                                modifier = Modifier.fillMaxWidth()
                                    .height(40.dp),
                            )
                            repeat(3) { index ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .height(40.dp)
                                        .background(DarkTheme.Grey1),
                                ) {
                                    val location = 1000.milliseconds
                                    Icon(
                                        "icons/diamond.svg",
                                        modifier = Modifier
                                            .size(20.dp)
                                            .layout { measurable, constraints ->
                                                val measured = measurable.measure(constraints)
                                                val bias = (location * index / duration).toFloat()
                                                val offsetX = (trackWidth.toPx() * bias).roundToInt()
                                                layout(measured.width, measured.height) {
                                                    measured.placeRelative(-measured.width / 2 + offsetX, 0)
                                                }
                                            },
                                        colorFilter = ColorFilter.tint(Color(0xFF9c3aef))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
