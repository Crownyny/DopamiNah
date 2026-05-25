package co.edu.unicauca.dopaminah.domain.repository

data class DailyDetailStats(
    val dateLabel: String,
    val firstUseTime: String,
    val avgSessionMinutes: Int,
    val mostUsedAppName: String,
    val mostUsedAppTime: String,
    val unlocks: Int,
    val totalTimeMillis: Long
)

data class MonitoringStats(
    val totalScreenTimeMillis: Long = 0L,
    val unlockCount: Int = 0,
    val lastResetDate: String = ""
)
