package co.edu.unicauca.dopaminah

import web.navigator.navigator

actual fun currentTimeMillis(): Long = js("Date.now()").unsafeCast<Long>()

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
