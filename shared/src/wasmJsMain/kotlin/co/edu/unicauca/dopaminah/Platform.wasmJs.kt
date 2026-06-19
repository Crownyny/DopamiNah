package co.edu.unicauca.dopaminah

import co.edu.unicauca.dopaminah.domain.model.TrustedContact

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
internal val currentTimeMillisJs: Double = js("Date.now()")

actual fun currentTimeMillis(): Long = currentTimeMillisJs.toLong()

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
actual fun currentLocalDayNumber(): Long {
    val offsetMin = js("new Date().getTimezoneOffset()").unsafeCast<Int>()
    return (currentTimeMillis() - offsetMin * 60_000L) / 86_400_000L
}

actual fun isFocusModeBlockingEnabled(): Boolean = false
actual fun setFocusModeBlockingEnabled(enabled: Boolean) {}
actual fun getFocusBlockedPackages(): Set<String> = emptySet()

actual fun getPlatformName(): String = "Web with Kotlin/Wasm"

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

private var trustedContactCache = TrustedContact()

actual fun loadTrustedContact(): TrustedContact = trustedContactCache

actual fun saveTrustedContact(contact: TrustedContact) {
    trustedContactCache = contact
}
