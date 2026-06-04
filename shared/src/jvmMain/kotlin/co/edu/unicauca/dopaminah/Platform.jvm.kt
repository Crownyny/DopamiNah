package co.edu.unicauca.dopaminah

import java.util.prefs.Preferences

actual fun getPlatformName(): String = "Java ${System.getProperty("java.version")}"
actual fun currentTimeMillis(): Long = System.currentTimeMillis()

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
