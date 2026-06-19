package co.edu.unicauca.dopaminah

import co.edu.unicauca.dopaminah.domain.model.TrustedContact
import kotlinx.browser.window
import web.navigator.navigator

actual fun currentTimeMillis(): Long = js("Date.now()").unsafeCast<Long>()

actual fun currentLocalDayNumber(): Long {
    val offsetMin = js("new Date().getTimezoneOffset()").unsafeCast<Int>()
    return (currentTimeMillis() - offsetMin * 60_000L) / 86_400_000L
}

actual fun isFocusModeBlockingEnabled(): Boolean = false
actual fun setFocusModeBlockingEnabled(enabled: Boolean) {}
actual fun getFocusBlockedPackages(): Set<String> = emptySet()

actual fun getPlatformName(): String {
    val userAgent = navigator.userAgent
    val browserList = listOf("Chrome", "Firefox", "Safari", "Edge")
    return userAgent.findAnyOf(browserList, ignoreCase = true)
        ?.let { (startIndex) -> userAgent.substring(startIndex).substringBefore(" ") }
        ?: "Unknown"
}

actual class DevicePreferences {
    private val map = mutableMapOf<String, String>()

    actual fun getInt(key: String, default: Int): Int = map[key]?.toIntOrNull() ?: default
    actual fun putInt(key: String, value: Int) { map[key] = value.toString() }
    actual fun getLong(key: String, default: Long): Long = map[key]?.toLongOrNull() ?: default
    actual fun putLong(key: String, value: Long) { map[key] = value.toString() }
    actual fun getBoolean(key: String, default: Boolean): Boolean = map[key]?.toBooleanStrictOrNull() ?: default
    actual fun putBoolean(key: String, value: Boolean) { map[key] = value.toString() }
    actual fun getString(key: String, default: String): String = map[key] ?: default
    actual fun putString(key: String, value: String) { map[key] = value }
}

@androidx.compose.runtime.Composable
actual fun PlatformStatusBarEffect(darkIcons: Boolean) {}

actual fun hasOverlayPermission(): Boolean = true
actual fun requestOverlayPermission() {}
actual fun isMonitoringServiceRunning(): Boolean = false
actual fun startMonitoringService() {}
actual fun stopMonitoringService() {}
actual fun addBypassApp(packageName: String) {}
actual fun isAppBypassed(packageName: String): Boolean = false
actual fun removeBypassApp(packageName: String) {}

actual fun loadTrustedContact(): TrustedContact {
    return try {
        val raw = kotlinx.browser.window.localStorage.getItem("dopaminah_trusted_contact") ?: ""
        if (raw.isBlank()) return TrustedContact()
        val parts = raw.split("|")
        TrustedContact(name = parts.getOrElse(0) { "" }, phone = parts.getOrElse(1) { "" })
    } catch (_: Exception) {
        TrustedContact()
    }
}

actual fun saveTrustedContact(contact: TrustedContact) {
    try {
        kotlinx.browser.window.localStorage.setItem("dopaminah_trusted_contact", "${contact.name}|${contact.phone}")
    } catch (_: Exception) {}
}
