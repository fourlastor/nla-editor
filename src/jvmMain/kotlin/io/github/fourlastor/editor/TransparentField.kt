package io.github.fourlastor.editor

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import io.kanro.compose.jetbrains.expui.style.LocalAreaColors
import io.kanro.compose.jetbrains.expui.style.LocalDefaultTextStyle

@Composable
fun <T> TransparentField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (T) -> Unit,
    textStyle: TextStyle = LocalDefaultTextStyle.current,
    validator: (String) -> T,
) {
    var text by remember(value) { mutableStateOf(value) }
    val dirty by remember(value, text) { derivedStateOf { value != text } }
    val color = LocalAreaColors.current.text
    val textStyleMerged = textStyle.merge(TextStyle(color = color))
    BasicTextField(
        value = text,
        onValueChange = { text = it },
        textStyle = textStyleMerged,
        cursorBrush = SolidColor(color),
        modifier = modifier.onFocusChanged {
            if (dirty && !it.hasFocus) {
                onValueChange(validator(text))
            }
        }
    )
}
