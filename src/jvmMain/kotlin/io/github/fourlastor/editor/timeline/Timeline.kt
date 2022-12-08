package io.github.fourlastor.editor.timeline

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.fourlastor.data.Animations
import io.github.fourlastor.data.Entities
import io.github.fourlastor.editor.DraggableHandle
import io.github.fourlastor.editor.KeyFrame
import io.github.fourlastor.editor.state.toEntitiesState
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
    animationId: Long,
    animations: Animations,
) {
    val entitiesState by remember(entities) { mutableStateOf(entities.toEntitiesState()) }
    val secondWidth = 300.dp
    val horizontalScrollState = rememberScrollState(0)
    val coroutineScope = rememberCoroutineScope()
    val trackWidth = secondWidth * duration.inWholeMilliseconds.toInt() / 1000

    Column(modifier = modifier) {
        HorizontalScrollbar(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            adapter = rememberScrollbarAdapter(horizontalScrollState)
        )
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val maxWidthDp = maxWidth
            val maxWidthPx = constraints.maxWidth
            val density = maxWidthPx / maxWidthDp.value
            val trackWidthPx = trackWidth.value * density
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
                Scrubber(trackWidth, trackWidthPx)
                Column(modifier = Modifier.fillMaxSize()) {
                    TimeIndicator(duration, secondWidth)
                    LazyColumn(
                        modifier = Modifier.width(trackWidth)
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        state = propertyListState,
                        userScrollEnabled = false,
                    ) {
                        items(
                            count = entitiesState.entities.size,
                            key = { entitiesState.entities[it].id }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Spacer(
                                    modifier = Modifier.fillMaxWidth()
                                        .height(40.dp),
                                )
                                // this should be 1 track per property in the entity
                                // 3 works because we have x,y,rotation
                                repeat(3) { index ->
                                    FrameTrack(
                                        duration = duration,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        val location = 1000.milliseconds * index
                                        KeyFrame(
                                            modifier = Modifier
                                                .position(location)
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
}

private fun TimeIndicator(duration: Duration, secondWidth: Dp) {
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
}

@Composable
private fun Scrubber(trackWidth: Dp, trackWidthPx: Float) {
    Row(
        modifier = Modifier
            .width(trackWidth)
            .zIndex(2f)
    ) {
        var scrubberOffset by remember { mutableStateOf(0f) }
        Spacer(modifier = Modifier.fillMaxWidth(scrubberOffset))
        DraggableHandle(
            orientation = Orientation.Vertical,
            color = Color.Red,
            size = 2.dp
        ) {
            val delta = it.x / trackWidthPx
            scrubberOffset += delta
        }
    }
}

@Composable
private fun FrameTrack(
    duration: Duration,
    modifier: Modifier = Modifier,
    content: @Composable AdvancementTrackScope.() -> Unit,
) {
    val scope = remember(duration) {
        object : AdvancementTrackScope {
            override val total: Duration
                get() = duration

        }
    }
    Layout(
        content = { scope.content() },
        modifier = modifier
            .height(40.dp)
            .background(DarkTheme.Grey1)
    ) { measurables, constraints ->
        val offsetY = constraints.maxHeight / 2
        val placeables = measurables.map { it.measure(constraints) to it.parentData as AdvancementParentData }
        layout(constraints.maxWidth, constraints.maxHeight) {
            fun Placeable.placeCenter(x: Int, y: Int) = place(
                -width / 2 + x,
                -height / 2 + y,
            )

            placeables.forEach { (placeable, data) ->
                val offsetX = (constraints.maxWidth * data.bias).roundToInt()
                placeable.placeCenter(offsetX, offsetY)
            }
        }
    }
}

@LayoutScopeMarker
@Immutable
interface AdvancementTrackScope {

    val total: Duration

    @Stable
    fun Modifier.position(position: Duration) = then(
        AdvancementParentData(
            bias = (position / total).toFloat()
        )
    )
}

private class AdvancementParentData(val bias: Float) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@AdvancementParentData

}