package io.github.fourlastor.editor

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors

@Composable
fun <T> TransparentField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (T) -> Unit,
    validator: (String) -> T,
) {
    var text by remember(value) { mutableStateOf(value) }
    val color = LocalAreaColors.current.text
    val textStyle = TextStyle(color = color)
    BasicTextField(
        value = text,
        onValueChange = { text = it },
        textStyle = textStyle,
        cursorBrush = SolidColor(color),
        modifier = modifier.onFocusChanged {
            if (!it.hasFocus) {
                onValueChange(validator(text))
            }
        }
    )
}
