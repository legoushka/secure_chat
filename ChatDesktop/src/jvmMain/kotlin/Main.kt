// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import Screens.ChatScreen
import Screens.LoginScreen
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.MenuBar

fun main() {
    val viewModel = ViewModel()
    application {
        ChatTheme(darkTheme = viewModel.isDarkThemeActivated.value) {
            val icon = painterResource("icon.png")
            if (viewModel.isLoginScreenActive.value) {
                Window(
                    onCloseRequest = ::exitApplication, title = "Login",
                    resizable = false,
                    icon = icon,
                    state = rememberWindowState(width = 360.dp, height = 660.dp),
                ) {
                    LoginScreen(viewModel)
                }
            } else {
                Window(
                    onCloseRequest = {
                        viewModel.closeApp()
                    },
                    title = "Chat",
                    resizable = false,
                    icon = icon,
                    state = rememberWindowState(width = 800.dp, height = 600.dp),
                ) {
                    ChatScreen(viewModel)
                }
            }

        }
    }
}

