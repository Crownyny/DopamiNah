package co.edu.unicauca.dopaminah.domain.model

data class AppUsageSummary(
    val packageName: String,
    val totalTimeForegroundMillis: Long,
    val unlockCount: Int,
    val lastTimeUsed: Long
)
