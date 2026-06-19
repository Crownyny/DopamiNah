package co.edu.unicauca.dopaminah

import androidx.compose.runtime.Composable
import co.edu.unicauca.dopaminah.domain.model.TrustedContact

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
 * Returns the number of days since Unix epoch (1970-01-01) in the device's **local** timezone.
 * Unlike [currentTimeMillis] / 86_400_000 (which uses UTC boundaries), this respects
 * the local date boundary, so streaks and daily counts work correctly across all timezones.
 */
expect fun currentLocalDayNumber(): Long

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

/** Load the user's trusted contact from persistent storage. */
expect fun loadTrustedContact(): TrustedContact

/** Save the user's trusted contact to persistent storage. */
expect fun saveTrustedContact(contact: TrustedContact)

/**
 * Returns whether focus mode app blocking is currently enabled.
 * When enabled, Android apps matching focus-blocked domains (YouTube, Instagram, etc.)
 * will be blocked by the AppLimitMonitoringService.
 */
expect fun isFocusModeBlockingEnabled(): Boolean

/**
 * Enables or disables focus mode app blocking.
 * Call this from the Settings screen when the user toggles focus mode.
 */
expect fun setFocusModeBlockingEnabled(enabled: Boolean)

/**
 * Returns the set of Android package names that should be blocked when focus mode is on.
 */
expect fun getFocusBlockedPackages(): Set<String>

/** Generate a random 6-digit approval code. */
fun generateApprovalCode(): String {
    val code = (100000..999999).random()
    return code.toString()
}

/** Verify an entered code against the stored code with expiry check. */
fun verifyApprovalCode(inputCode: String, storedCode: String, expiryTimestamp: Long): Boolean {
    if (inputCode.isBlank() || storedCode.isBlank()) return false
    if (currentTimeMillis() > expiryTimestamp) return false
    return inputCode.trim() == storedCode.trim()
}

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
