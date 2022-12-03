package io.github.fourlastor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PropertiesPane(
    modifier: Modifier,
) {
    Box(modifier = modifier.background(Color(0.4f, 0.8f, 0.5f)))
}
