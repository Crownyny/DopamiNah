package co.edu.unicauca.dopaminah

import co.edu.unicauca.dopaminah.domain.model.TrustedContact
import platform.Foundation.NSDate
import platform.Foundation.NSTimeZone
import platform.Foundation.NSUserDefaults

actual fun getPlatformName(): String = "iOS ${platform.UIKit.UIDevice.currentDevice.systemVersion}"
actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

actual fun currentLocalDayNumber(): Long {
    val tz = NSTimeZone.localTimeZone
    val offset = tz.secondsFromGMT * 1000L
    return (currentTimeMillis() + offset) / 86_400_000L
}

actual fun isFocusModeBlockingEnabled(): Boolean = false
actual fun setFocusModeBlockingEnabled(enabled: Boolean) {}
actual fun getFocusBlockedPackages(): Set<String> = emptySet()

actual class DevicePreferences {
    private val defaults = NSUserDefaults(suiteName = "group.com.dopaminah") ?: NSUserDefaults.standardUserDefaults

    actual fun getInt(key: String, default: Int): Int = defaults.integerForKey(key).toInt().takeIf { it != 0 } ?: default
    actual fun putInt(key: String, value: Int) { defaults.setInteger(value.toLong(), key) }
    actual fun getLong(key: String, default: Long): Long = (defaults.doubleForKey(key).toLong()).takeIf { it != 0L } ?: default
    actual fun putLong(key: String, value: Long) { defaults.setDouble(value.toDouble(), key) }
    actual fun getBoolean(key: String, default: Boolean): Boolean =
        if (defaults.objectForKey(key) != null) defaults.boolForKey(key) else default
    actual fun putBoolean(key: String, value: Boolean) { defaults.setBool(value, key) }
    actual fun getString(key: String, default: String): String = defaults.stringForKey(key) ?: default
    actual fun putString(key: String, value: String) { defaults.setObject(value, key) }
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
    val defaults = NSUserDefaults.standardUserDefaults
    return TrustedContact(
        name = defaults.stringForKey("trusted_contact_name") ?: "",
        phone = defaults.stringForKey("trusted_contact_phone") ?: ""
    )
}

actual fun saveTrustedContact(contact: TrustedContact) {
    val defaults = NSUserDefaults.standardUserDefaults
    defaults.setObject(contact.name, forKey = "trusted_contact_name")
    defaults.setObject(contact.phone, forKey = "trusted_contact_phone")
}
