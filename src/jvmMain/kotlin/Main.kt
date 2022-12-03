import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.fourlastor.editor.AnimationEditor

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        AnimationEditor()
    }
}
