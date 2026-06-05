package co.edu.unicauca.dopaminah.domain.repository

/** Detailed stats for a single day, used in the stats screen's daily detail view. */
data class DailyDetailStats(
    val dateLabel: String,
    val firstUseTime: String,
    val avgSessionMinutes: Int,
    val mostUsedAppName: String,
    val mostUsedAppTime: String,
    val unlocks: Int,
    val totalTimeMillis: Long
)

/** Real-time monitoring snapshot: total screen time, unlock count, and last reset date. */
data class MonitoringStats(
    val totalScreenTimeMillis: Long = 0L,
    val unlockCount: Int = 0,
    val lastResetDate: String = ""
)
