package co.edu.unicauca.dopaminah

import androidx.compose.runtime.Composable

expect fun getPlatformName(): String
expect fun currentTimeMillis(): Long

@Composable
expect fun PlatformStatusBarEffect(darkIcons: Boolean)

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
