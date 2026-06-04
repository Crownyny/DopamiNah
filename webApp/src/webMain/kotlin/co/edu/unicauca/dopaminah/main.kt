package co.edu.unicauca.dopaminah

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.edu.unicauca.dopaminah.ui.navigation.AppTab
import co.edu.unicauca.dopaminah.ui.navigation.PermissionState
import kotlinx.browser.window

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun registerServiceWorker() {
    try {
        window.navigator.serviceWorker.register("/sw.js")
    } catch (_: Exception) { }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    registerServiceWorker()
    ComposeViewport {
        App(
            permissionState = PermissionState(hasUsagePermission = true),
            hiddenTabs = setOf(AppTab.DASHBOARD, AppTab.STATS)
        )
    }
}