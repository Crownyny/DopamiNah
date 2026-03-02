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
}
