package co.edu.unicauca.dopaminah

import android.content.Context
import android.content.SharedPreferences

actual fun getPlatformName(): String = "Android ${android.os.Build.VERSION.SDK_INT}"

actual class DevicePreferences(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dopaminah_prefs", Context.MODE_PRIVATE)

    actual fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)
    actual fun putInt(key: String, value: Int) { prefs.edit().putInt(key, value).apply() }
    actual fun getLong(key: String, default: Long): Long = prefs.getLong(key, default)
    actual fun putLong(key: String, value: Long) { prefs.edit().putLong(key, value).apply() }
    actual fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    actual fun putBoolean(key: String, value: Boolean) { prefs.edit().putBoolean(key, value).apply() }
    actual fun getString(key: String, default: String): String = prefs.getString(key, default) ?: default
    actual fun putString(key: String, value: String) { prefs.edit().putString(key, value).apply() }
}
