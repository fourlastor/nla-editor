package io.github.fourlastor.system.extension

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun Duration.scale(amount: Float): Duration = this * amount.toDouble()
fun Duration.roundToMilliseconds(): Duration = inWholeMilliseconds.milliseconds
