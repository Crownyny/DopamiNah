@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package co.edu.unicauca.dopaminah

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.edu.unicauca.dopaminah.ui.navigation.AppTab
import co.edu.unicauca.dopaminah.ui.navigation.PermissionState
import kotlinx.browser.window
import kotlinx.coroutines.delay

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
        SyncAwareApp()
    }
}

@Composable
private fun SyncAwareApp() {
    LaunchedEffect(Unit) {
        while (true) {
            val json = pollSync()
            if (json != null) {
                val goals = parseGoals(json)
                for ((url, minutes) in goals) {
                    SyncBridge.onIncomingSync?.invoke(url, minutes)
                }
            }
            delay(2000L)
        }
    }

    App(
        permissionState = PermissionState(hasUsagePermission = true),
        hiddenTabs = setOf(AppTab.DASHBOARD, AppTab.STATS),
        useWebGoals = true,
        onSyncGoalsToExtension = { json ->
            sendGoals(json)
        }
    )
}

private fun pollSync(): String? = js("window.__dopaminahPollSync()")

private fun sendGoals(json: String): Unit = js("window.__dopaminahPostGoals(json)")

private fun parseGoalsInit(json: String): Int = js("window.__dopaminahParseGoalsJson(json)")

private fun goalDomain(index: Int): String? = js("window.__dopaminahGoalDomain(index)")

private fun goalDisplayUrl(index: Int): String? = js("window.__dopaminahGoalDisplayUrl(index)")

private fun goalMinutes(index: Int): Int = js("window.__dopaminahGoalMinutes(index)")

private data class ParsedGoal(val url: String, val minutes: Int)

private fun parseGoals(json: String): List<ParsedGoal> {
    val result = mutableListOf<ParsedGoal>()
    val count = parseGoalsInit(json)
    for (i in 0 until count) {
        try {
            val domain = goalDomain(i) ?: continue
            val displayUrl = goalDisplayUrl(i) ?: domain
            val minutes = goalMinutes(i)
            result.add(ParsedGoal(displayUrl, minutes))
        } catch (_: Exception) { }
    }
    return result
}
