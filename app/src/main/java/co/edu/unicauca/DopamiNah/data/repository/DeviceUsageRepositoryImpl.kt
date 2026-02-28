package co.edu.unicauca.DopamiNah.data.repository

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import co.edu.unicauca.DopamiNah.domain.model.AppUsageSummary
import co.edu.unicauca.DopamiNah.domain.repository.DeviceUsageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class DeviceUsageRepositoryImpl(
    private val context: Context
) : DeviceUsageRepository {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    override suspend fun getDailyUsageStats(): List<AppUsageSummary> = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission()) return@withContext emptyList()

        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        stats.filter { it.totalTimeInForeground > 0 }
            .map { usageStat ->
                val unlockCount = getUnlockCount(usageStat.packageName, startTime, endTime)
                AppUsageSummary(
                    packageName = usageStat.packageName,
                    totalTimeForegroundMillis = usageStat.totalTimeInForeground,
                    unlockCount = unlockCount,
                    lastTimeUsed = usageStat.lastTimeUsed
                )
            }.sortedByDescending { it.totalTimeForegroundMillis }
    }

    override suspend fun getDailyDeviceUnlocks(): Int = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission()) return@withContext 0
        
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        countDeviceUnlocks(startTime, endTime)
    }

    override suspend fun getYesterdayDeviceUnlocks(): Int = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission()) return@withContext 0

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val endTime = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

        countDeviceUnlocks(startTime, endTime)
    }

    private fun countDeviceUnlocks(startTime: Long, endTime: Long): Int {
        var count = 0
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()
        var lastEventTime = 0L
        
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            // EventType 15 is SCREEN_INTERACTIVE (Device wake up / unlock)
            // EventType 18 is KEYGUARD_HIDDEN (Unlock)
            // EventType 1 is ACTIVITY_RESUMED
            
            // If the device doesn't reliably send 15 or 18, we can infer a "session start"
            // if an activity is resumed and it has been at least 1 minute since the last resumed activity.
            if (event.eventType == 15 || event.eventType == 18) {
                 if (event.eventType == 15) count++
            } else if (event.eventType == 1) { // ACTIVITY_RESUMED
                // Backup metric: if no screen interactive events were counted, 
                // we treat resumed activities with a gap > 5 mins as a new 'unlock/session'
                if (event.timeStamp - lastEventTime > 5 * 60 * 1000) {
                    count++
                }
                lastEventTime = event.timeStamp
            }
        }
        return count
    }

    override fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun getUnlockCount(packageName: String, startTime: Long, endTime: Long): Int {
        // En una implementación real más compleja se usa queryEvents.
        // Por simplicidad, aquí retornamos 0, pero la lógica de contar "ACTIVITY_RESUMED" iría aquí.
        var count = 0
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.packageName == packageName && event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED) {
                count++
            }
        }
        return count
    }
}
