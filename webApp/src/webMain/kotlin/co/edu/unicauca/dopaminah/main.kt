@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package co.edu.unicauca.dopaminah

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
    val webGoalsPrefs = remember { DevicePreferences() }

    LaunchedEffect(Unit) {
        requestFullSync()
        loadCachedGoals()
        loadCachedDomainTime()
        while (true) {
            val json = pollSync()
            if (json.isNotEmpty()) {
                saveToCache(json)
                val goals = parseGoals(json)
                for ((url, minutes) in goals) {
                    SyncBridge.onIncomingSync?.invoke(url, minutes)
                }
                loadCachedDomainTime()
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
        },
        webGoalsPrefs = webGoalsPrefs
    )
}

private val jsRequestFullSync: () -> Unit = js("() => window.__dopaminahRequestFullSync()")
private val jsLoadCachedGoals: () -> String = js("() => window.__dopaminahLoadCachedGoals() || ''")
private val jsSaveToCache: (String) -> Unit = js("(json) => window.__dopaminahSaveGoals(json)")
private val jsPollSync: () -> String = js("() => window.__dopaminahPollSync() || ''")
private val jsSendGoals: (String) -> Unit = js("(json) => window.__dopaminahPostGoals(json)")
private val jsParseGoalsInit: (String) -> Int = js("(json) => window.__dopaminahParseGoalsJson(json)")
private val jsGoalDomain: (Int) -> String = js("(i) => window.__dopaminahGoalDomain(i) || ''")
private val jsGoalDisplayUrl: (Int) -> String = js("(i) => window.__dopaminahGoalDisplayUrl(i) || ''")
private val jsGoalMinutes: (Int) -> Int = js("(i) => window.__dopaminahGoalMinutes(i)")
private val jsLoadCachedDomainTime: () -> String = js("() => window.__dopaminahLoadCachedDomainTime() || ''")
private val jsDomainTimeCount: (String) -> Int = js("(json) => window.__dopaminahDomainTimeCount(json)")
private val jsDomainTimeDomain: (String, Int) -> String = js("(json, i) => window.__dopaminahDomainTimeDomain(json, i) || ''")
private val jsDomainTimeMinutes: (String, Int) -> Int = js("(json, i) => window.__dopaminahDomainTimeMinutes(json, i)")

private fun requestFullSync() { jsRequestFullSync() }

private fun loadCachedGoals() {
    val json = jsLoadCachedGoals()
    if (json.isNotEmpty()) {
        val goals = parseGoals(json)
        for ((url, minutes) in goals) {
            SyncBridge.onIncomingSync?.invoke(url, minutes)
        }
    }
}

private fun saveToCache(json: String) { jsSaveToCache(json) }

private fun pollSync(): String {
    return jsPollSync()
}

private fun sendGoals(json: String) { jsSendGoals(json) }

private fun parseGoalsInit(json: String): Int = jsParseGoalsInit(json)

private fun goalDomain(index: Int): String = jsGoalDomain(index)

private fun goalDisplayUrl(index: Int): String = jsGoalDisplayUrl(index)

private fun goalMinutes(index: Int): Int = jsGoalMinutes(index)

private fun loadCachedDomainTime() {
    val json = jsLoadCachedDomainTime()
    if (json.isEmpty()) return
    val count = jsDomainTimeCount(json)
    for (i in 0 until count) {
        try {
            val domain = jsDomainTimeDomain(json, i)
            if (domain.isEmpty()) continue
            val minutes = jsDomainTimeMinutes(json, i)
            SyncBridge.onDomainTimeSync?.invoke(domain, minutes)
        } catch (_: Exception) { }
    }
}

private data class ParsedGoal(val url: String, val minutes: Int)

private fun parseGoals(json: String): List<ParsedGoal> {
    val result = mutableListOf<ParsedGoal>()
    val count = parseGoalsInit(json)
    for (i in 0 until count) {
        try {
            val domain = goalDomain(i)
            if (domain.isEmpty()) continue
            val displayUrl = goalDisplayUrl(i)
            val minutes = goalMinutes(i)
            result.add(ParsedGoal(displayUrl.ifEmpty { domain }, minutes))
        } catch (_: Exception) { }
    }
    return result
}
