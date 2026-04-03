package co.edu.unicauca.dopaminah.domain.repository

import kotlinx.coroutines.flow.Flow

data class MonitoringStats(
    val totalScreenTimeMillis: Long = 0L,
    val unlockCount: Int = 0,
    val lastResetDate: String = "" // "yyyy-MM-dd"
)

interface UsageMonitoringRepository {
    fun getMonitoringStats(): Flow<MonitoringStats>
    suspend fun updateScreenTime(durationMillis: Long)
    suspend fun incrementUnlockCount()
    suspend fun resetDailyStats(date: String)
    suspend fun setLastScreenOnTime(timestamp: Long)
    suspend fun getLastScreenOnTime(): Long
    
    suspend fun isAlertNotified(alertId: String): Boolean
    suspend fun markAlertNotified(alertId: String)
}
