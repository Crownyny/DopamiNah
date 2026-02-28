package co.edu.unicauca.DopamiNah.domain.repository

import co.edu.unicauca.DopamiNah.domain.model.AppUsageSummary
import kotlinx.coroutines.flow.Flow

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
