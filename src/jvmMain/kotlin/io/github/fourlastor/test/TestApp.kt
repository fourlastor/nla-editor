package io.github.fourlastor.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.rememberWindowState
import io.github.fourlastor.system.layout.GridUi
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import io.kanro.compose.jetbrains.expui.window.JBWindow
import kotlin.system.exitProcess

@Suppress("unused") // used to test things out without the whole app starting
@Composable
fun ApplicationScope.TestApp() {
    JBWindow(
        title = "NLA Editor",
        theme = DarkTheme,
        state = rememberWindowState(size = DpSize(900.dp, 700.dp)),
        onCloseRequest = {
            exitApplication()
            exitProcess(0)
        },
    ) {
        GridUi(
            modifier = Modifier.fillMaxSize(),
            topLeft = {
                FilledWith(Color.Red)
            },
            topRight = {
                FilledWith(Color.Green)
            },
            bottomLeft = {
                FilledWith(Color.Yellow)
            },
            bottomRight = {
                FilledWith(Color.Blue)
            }
        )
    }
}

@Composable
private fun BoxScope.FilledWith(color: Color) {
    Box(Modifier.background(color).matchParentSize()) {

    }
}
