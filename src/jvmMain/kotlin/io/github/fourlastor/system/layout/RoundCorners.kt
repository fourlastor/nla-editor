package io.github.fourlastor.system.layout

enum class RoundCorners(
    val start: Boolean,
    val end: Boolean,
) {
    Both(true, true),
    None(false, false),
    Start(true, false),
    End(false, true),
}
