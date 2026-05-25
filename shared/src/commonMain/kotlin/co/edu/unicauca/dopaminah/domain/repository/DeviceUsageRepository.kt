package co.edu.unicauca.dopaminah.domain.repository

import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary

interface DeviceUsageRepository {
    suspend fun getDailyUsageStats(): List<AppUsageSummary>
    suspend fun getDailyDeviceUnlocks(): Int
    suspend fun getYesterdayDeviceUnlocks(): Int
    fun hasUsageStatsPermission(): Boolean
    suspend fun getAverageUsageMillis(days: Int): Long
    suspend fun getAverageUnlocks(days: Int): Int
    suspend fun getDailyUsageForLastDays(days: Int): List<Long>
    suspend fun getAverageUsagePerApp(days: Int, limit: Int = 8): List<Pair<String, Long>>
    suspend fun getDailyDetails(dayOffset: Int): DailyDetailStats
    suspend fun getHourlyUsage(days: Int): List<Float>
}
