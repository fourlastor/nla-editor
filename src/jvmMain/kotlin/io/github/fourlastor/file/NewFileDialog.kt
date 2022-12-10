package io.github.fourlastor.file

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import io.github.fourlastor.system.layout.RoundCorners
import io.github.fourlastor.system.layout.roundedCorners
import io.kanro.compose.jetbrains.expui.control.Label
import io.kanro.compose.jetbrains.expui.control.themedSvgResource
import java.io.File

@Composable
fun NewFileDialog(
    state: FileDialogState.Loaded,
    onCloseRequest: (result: File?) -> Unit
) {
    var visible by remember { mutableStateOf(true) }
    Dialog(
        visible = visible,
        onCloseRequest = {
            visible = false
            onCloseRequest(null)
        },
        state = rememberDialogState(width = 800.dp, height = 600.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val bg1 = remember { Color(0xFF4b4b4b) }
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(bg1).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Border {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                        ) {
                            ImageButton(
                                image = "icons/arrow_back.svg",
                                contentDescription = "Back",
                                roundCorners = RoundCorners.Start,
                            )
                            ImageButton(
                                image = "icons/arrow_upward.svg",
                                contentDescription = "Up",
                            )
                            ImageButton(
                                image = "icons/refresh.svg",
                                contentDescription = "Refresh",
                                roundCorners = RoundCorners.End,
                            )
                        }
                    }
                    val modifier = Modifier.weight(1f)
                    DarkField("/", {}, modifier)
                }
                FileList(
                    state = state,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun Border(
    color: Color = Color.Unspecified,
    width: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val borderColor = color.takeOrElse { Color(0xFF3f3f3f) }
    Box(
        modifier = Modifier
            .roundedCorners(borderColor, cornerRadius = 2.dp)
            .padding(width),
        content = content,
    )
}

@Composable
private fun DarkField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default
) {
    val style = textStyle.merge(
        TextStyle(color = Color.White, fontSize = 16.sp)
    )
    Box(modifier = modifier.roundedCorners(Color.Black)) {
        BasicTextField(
            value,
            onValueChange,
            textStyle = style,
            cursorBrush = SolidColor(Color.White),
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 2.dp
            )
        )
    }
}

@Composable
private fun FileList(
    state: FileDialogState.Loaded,
    modifier: Modifier = Modifier,
) {
    val bg = remember { Color(0xff292929) }
    val headerBg = remember { Color(0xff3e3e3e) }
    val oddBg = remember { Color(0xff333333) }
    val evenBg = remember { Color(0xff2c2c2c) }
    val folderColor = remember { Color(0xFFbfa462) }
    val fontSize = 16.sp
    Column(
        modifier = modifier.background(bg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp)
                .background(headerBg)
                .padding(2.dp)
        ) {
            Label("Name", fontSize = fontSize)
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            state.files.forEachIndexed { i, file ->
                val bgColor = if (i % 2 == 0) evenBg else oddBg
                item(key = file.name) {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(bgColor)
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (file is FileDialogEntry.Folder) {
                            Image(
                                themedSvgResource("icons/folder.svg"),
                                null,
                                colorFilter = ColorFilter.tint(folderColor),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Label(file.name, fontSize = fontSize)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().background(headerBg).padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DarkField(
                value = "",
                onValueChange = {},
                modifier = Modifier.weight(1f),
            )
            TextButton("Cancel")
            TextButton("Open", bgColor = Color(0xff5680c2))
        }
    }
}

@Composable
private fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    bgColor: Color = Color.Unspecified,
) {
    Button(
        modifier = modifier,
        radius = 2.dp,
        color = bgColor,
    ) {
        BasicText(
            text = text,
            modifier = Modifier.padding(3.dp),
            style = TextStyle.Default.copy(fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Thin)
        )
    }
}

@Composable
private fun ImageButton(
    image: String,
    contentDescription: String,
    roundCorners: RoundCorners = RoundCorners.None,
) {
    val color = remember { Color(0xffe3e3e3) }
    Button(
        radius = 2.dp,
        roundCorners,
    ) {
        Image(
            painter = themedSvgResource(image),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
private fun Button(
    radius: Dp,
    roundCorners: RoundCorners = RoundCorners.Both,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit,
) {
    val bg = color.takeOrElse { Color(0xFF585858) }
    Box(
        modifier = modifier.roundedCorners(
            color = bg,
            cornerRadius = radius,
            roundCorners = roundCorners
        ),
        content = content,
    )
}
