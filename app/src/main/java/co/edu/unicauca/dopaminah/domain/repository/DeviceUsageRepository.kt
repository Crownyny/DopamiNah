package co.edu.unicauca.dopaminah.domain.repository

import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary

interface DeviceUsageRepository {
    /**
     * Obtains the usage stats for the current day.
     */
    suspend fun getDailyUsageStats(): List<AppUsageSummary>
    
    /**
     * Gets the number of times the device was unlocked today.
     */
    suspend fun getDailyDeviceUnlocks(): Int

    /**
     * Gets the number of times the device was unlocked yesterday.
     */
    suspend fun getYesterdayDeviceUnlocks(): Int
    
    /**
     * Checks if the user has granted the PACKAGE_USAGE_STATS permission.
     */
    fun hasUsageStatsPermission(): Boolean

    /**
     * Gets the average daily usage time in milliseconds over the last [days] days.
     */
    suspend fun getAverageUsageMillis(days: Int): Long

    /**
     * Gets the average daily unlocks over the last [days] days.
     */
    suspend fun getAverageUnlocks(days: Int): Int

    /**
     * Gets total usage time in milliseconds per day for the last [days] days.
     * Returns a list of durations ordered from oldest to newest (today).
     */
    suspend fun getDailyUsageForLastDays(days: Int): List<Long>

    /**
     * Gets average daily usage in milliseconds for the top apps over the last [days] days.
     * Returns a list of pairs (appName, avgMillisPerDay) sorted descending, capped at [limit].
     */
    suspend fun getAverageUsagePerApp(days: Int, limit: Int = 8): List<Pair<String, Long>>
}
