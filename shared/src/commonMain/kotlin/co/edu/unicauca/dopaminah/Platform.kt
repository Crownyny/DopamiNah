package co.edu.unicauca.dopaminah

import androidx.compose.runtime.Composable

/**
 * Returns a human-readable platform name (e.g. "Android 14", "iOS 17.0").
 * Actual implementations in each platform source set.
 */
expect fun getPlatformName(): String

/**
 * Returns the current time in milliseconds since Unix epoch.
 * Actual implementations use System.currentTimeMillis(), NSDate(), Date.now(), etc.
 */
expect fun currentTimeMillis(): Long

/**
 * Composable that adjusts the system status bar appearance.
 * Only implemented on Android (changes status bar icon colors); no-op elsewhere.
 */
@Composable
expect fun PlatformStatusBarEffect(darkIcons: Boolean)

expect fun hasOverlayPermission(): Boolean
expect fun requestOverlayPermission()
expect fun isMonitoringServiceRunning(): Boolean
expect fun startMonitoringService()
expect fun stopMonitoringService()
expect fun addBypassApp(packageName: String)
expect fun isAppBypassed(packageName: String): Boolean
expect fun removeBypassApp(packageName: String)

/**
 * Platform-native key-value storage.
 *
 * Actual implementations:
 * - Android: SharedPreferences ("dopaminah_prefs")
 * - iOS: NSUserDefaults
 * - Desktop (JVM): java.util.prefs.Preferences
 * - JS/WasmJS: In-memory MutableMap
 */
expect class DevicePreferences {
    fun getInt(key: String, default: Int): Int
    fun putInt(key: String, value: Int)
    fun getLong(key: String, default: Long): Long
    fun putLong(key: String, value: Long)
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
}
