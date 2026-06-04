package co.edu.unicauca.dopaminah

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.edu.unicauca.dopaminah.ui.navigation.PermissionState

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        App(
            permissionState = PermissionState(hasUsagePermission = true)
        )
    }
}