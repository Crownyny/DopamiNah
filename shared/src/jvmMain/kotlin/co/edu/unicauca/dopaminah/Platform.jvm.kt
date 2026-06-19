package co.edu.unicauca.dopaminah

import co.edu.unicauca.dopaminah.domain.model.TrustedContact
import java.util.prefs.Preferences

actual fun getPlatformName(): String = "Java ${System.getProperty("java.version")}"
actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun currentLocalDayNumber(): Long {
    val cal = java.util.Calendar.getInstance()
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    return cal.timeInMillis / 86_400_000L
}

actual fun isFocusModeBlockingEnabled(): Boolean = false
actual fun setFocusModeBlockingEnabled(enabled: Boolean) {}
actual fun getFocusBlockedPackages(): Set<String> = emptySet()

actual class DevicePreferences {
    private val prefs: Preferences = Preferences.userNodeForPackage(DevicePreferences::class.java)

    actual fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)
    actual fun putInt(key: String, value: Int) { prefs.putInt(key, value) }
    actual fun getLong(key: String, default: Long): Long = prefs.getLong(key, default)
    actual fun putLong(key: String, value: Long) { prefs.putLong(key, value) }
    actual fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    actual fun putBoolean(key: String, value: Boolean) { prefs.putBoolean(key, value) }
    actual fun getString(key: String, default: String): String = prefs.get(key, default)
    actual fun putString(key: String, value: String) { prefs.put(key, value) }
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
    val prefs = Preferences.userNodeForPackage(DevicePreferences::class.java)
    return TrustedContact(
        name = prefs.get("trusted_contact_name", ""),
        phone = prefs.get("trusted_contact_phone", "")
    )
}

actual fun saveTrustedContact(contact: TrustedContact) {
    val prefs = Preferences.userNodeForPackage(DevicePreferences::class.java)
    prefs.put("trusted_contact_name", contact.name)
    prefs.put("trusted_contact_phone", contact.phone)
}
