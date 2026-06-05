package co.edu.unicauca.dopaminah

import platform.Foundation.NSDate
import platform.Foundation.NSUserDefaults

actual fun getPlatformName(): String = "iOS ${platform.UIKit.UIDevice.currentDevice.systemVersion}"
actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

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
