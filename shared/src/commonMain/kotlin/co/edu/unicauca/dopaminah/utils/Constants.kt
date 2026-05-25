package co.edu.unicauca.dopaminah.utils

object Constants {
    const val PREFS_KEY_STREAK = "streak"
    const val PREFS_KEY_TOTAL_POINTS = "total_points"
    const val PREFS_KEY_LAST_OPENED = "last_opened_timestamp"
    const val PREFS_KEY_BEST_STREAK = "best_streak"
    const val PREFS_KEY_DARK_MODE = "dark_mode"
    const val PREFS_KEY_NOTIFICATIONS = "notifications_enabled"
    const val PREFS_KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    const val NOTIF_CHANNEL_ID = "usage_alerts"
    const val NOTIF_CHANNEL_NAME = "Alertas de Uso"
    const val NOTIF_CHANNEL_DESC = "Notificaciones sobre límites de uso de aplicaciones y pantalla."
    const val NOTIF_ID_SCREEN_TIME = 1001
    const val NOTIF_ID_UNLOCK_COUNT = 1002
    const val NOTIF_ID_APP_USAGE = 1003
    const val NOTIF_ID_APP_OPEN = 1004
    const val NOTIF_ID_SERVICE = 1000

    const val DATABASE_NAME = "dopaminah_db"
    const val PREFS_NAME = "dopaminah_prefs"
    const val APP_GROUP_ID = "group.com.dopaminah"
}
