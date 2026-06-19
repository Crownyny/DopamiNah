package co.edu.unicauca.dopaminah

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import co.edu.unicauca.dopaminah.domain.model.TrustedContact

actual fun getPlatformName(): String = "Android ${android.os.Build.VERSION.SDK_INT}"
actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun currentLocalDayNumber(): Long {
    val cal = java.util.Calendar.getInstance()
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    return cal.timeInMillis / 86_400_000L
}

private var focusModeBlockingEnabled = false

actual fun isFocusModeBlockingEnabled(): Boolean = focusModeBlockingEnabled

actual fun setFocusModeBlockingEnabled(enabled: Boolean) {
    focusModeBlockingEnabled = enabled
    if (!enabled) {
        bypassedApps.clear()
    }
}

actual fun getFocusBlockedPackages(): Set<String> = FOCUS_BLOCKED_PACKAGES

/** Mapping from focus mode default domains to Android package names. */
private val FOCUS_BLOCKED_PACKAGES: Set<String> = setOf(
    "com.google.android.youtube",
    "com.reddit.frontpage",
    "com.twitter.android",
    "com.instagram.android",
    "com.facebook.katana",
    "com.zhiliaoapp.musically",
    "com.netflix.mediaclient",
    "tv.twitch.android.app",
    "com.discord",
)

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

private var androidAppContext: Context? = null
private val bypassedApps = mutableSetOf<String>()
private var isServiceRunning = false

fun setAppContext(context: Context) {
    androidAppContext = context.applicationContext
}

fun getAppContext(): Context {
    return androidAppContext ?: throw IllegalStateException("App context is not initialized. Please call setAppContext first.")
}

fun setServiceRunning(running: Boolean) {
    isServiceRunning = running
}

actual fun hasOverlayPermission(): Boolean {
    val context = getAppContext()
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        android.provider.Settings.canDrawOverlays(context)
    } else {
        true
    }
}

actual fun requestOverlayPermission() {
    val context = getAppContext()
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        val intent = android.content.Intent(
            android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:${context.packageName}")
        ).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

actual fun isMonitoringServiceRunning(): Boolean {
    return isServiceRunning
}

actual fun startMonitoringService() {
    val context = getAppContext()
    val clazz = Class.forName("co.edu.unicauca.dopaminah.AppLimitMonitoringService")
    val intent = android.content.Intent(context, clazz)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

actual fun stopMonitoringService() {
    val context = getAppContext()
    val clazz = Class.forName("co.edu.unicauca.dopaminah.AppLimitMonitoringService")
    val intent = android.content.Intent(context, clazz)
    context.stopService(intent)
}

actual fun addBypassApp(packageName: String) {
    bypassedApps.add(packageName)
}

actual fun isAppBypassed(packageName: String): Boolean {
    return bypassedApps.contains(packageName)
}

actual fun removeBypassApp(packageName: String) {
    bypassedApps.remove(packageName)
}

actual fun loadTrustedContact(): TrustedContact {
    val prefs = getAppContext().getSharedPreferences("dopaminah_prefs", Context.MODE_PRIVATE)
    return TrustedContact(
        name = prefs.getString("trusted_contact_name", "") ?: "",
        phone = prefs.getString("trusted_contact_phone", "") ?: ""
    )
}

actual fun saveTrustedContact(contact: TrustedContact) {
    val prefs = getAppContext().getSharedPreferences("dopaminah_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("trusted_contact_name", contact.name).apply()
    prefs.edit().putString("trusted_contact_phone", contact.phone).apply()
}

@Composable
actual fun PlatformStatusBarEffect(darkIcons: Boolean) {
    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (view.context as? android.app.Activity)?.window
            if (window != null) {
                val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = darkIcons
            }
        }
    }
}
