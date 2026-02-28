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
