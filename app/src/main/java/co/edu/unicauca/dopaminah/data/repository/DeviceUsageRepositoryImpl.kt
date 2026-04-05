package co.edu.unicauca.dopaminah.data.repository

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.repository.DailyDetailStats
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
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

        val aggregatedStats: Map<String, UsageStats> = usageStatsManager
            .queryAndAggregateUsageStats(startTime, endTime)

        val unlockCounts = mutableMapOf<String, Int>()
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED) {
                unlockCounts[event.packageName] = (unlockCounts[event.packageName] ?: 0) + 1
            }
        }

        val packageManager = context.packageManager

        aggregatedStats.values
            .filter { it.totalTimeInForeground > 0 }
            .map { usageStat ->
                val packageName = usageStat.packageName
                val appName = try {
                    val appInfo = packageManager.getApplicationInfo(packageName, 0)
                    packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    packageName
                }

                AppUsageSummary(
                    packageName = packageName,
                    appName = appName,
                    totalTimeForegroundMillis = usageStat.totalTimeInForeground,
                    unlockCount = unlockCounts[packageName] ?: 0,
                    lastTimeUsed = usageStat.lastTimeUsed
                )
            }.sortedByDescending { it.totalTimeForegroundMillis }
    }

    override suspend fun getDailyDeviceUnlocks(): Int = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission()) return@withContext 0
        
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
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

    override suspend fun getAverageUsageMillis(days: Int): Long = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission() || days <= 0) return@withContext 0L

        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startTime = calendar.timeInMillis

        val aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
        val totalTime = aggregatedStats.values.sumOf { it.totalTimeInForeground }
        
        totalTime / days
    }

    override suspend fun getAverageUnlocks(days: Int): Int = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission() || days <= 0) return@withContext 0
        
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startTime = calendar.timeInMillis

        val totalUnlocks = countDeviceUnlocks(startTime, endTime)
        
        totalUnlocks / days
    }

    override suspend fun getDailyUsageForLastDays(days: Int): List<Long> = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission() || days <= 0) return@withContext emptyList()
        
        val usagePerDay = mutableListOf<Long>()
        
        for (i in (days - 1) downTo 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis
            
            val endCalendar = calendar.clone() as Calendar
            endCalendar.add(Calendar.DAY_OF_YEAR, 1)
            val endTime = endCalendar.timeInMillis
            
            val aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
            val totalTime = aggregatedStats.values.sumOf { it.totalTimeInForeground }
            usagePerDay.add(totalTime)
        }
        
        usagePerDay
    }

    override suspend fun getAverageUsagePerApp(days: Int, limit: Int): List<Pair<String, Long>> = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission() || days <= 0) return@withContext emptyList()

        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startTime = calendar.timeInMillis

        val aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
        val packageManager = context.packageManager

        aggregatedStats.values
            .filter { it.totalTimeInForeground > 0 }
            .map { stat ->
                val appName = try {
                    val appInfo = packageManager.getApplicationInfo(stat.packageName, 0)
                    packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    stat.packageName
                }
                appName to (stat.totalTimeInForeground / days)
            }
            .sortedByDescending { it.second }
            .take(limit)
    }

    override suspend fun getDailyDetails(dayOffset: Int): DailyDetailStats = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -dayOffset)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endTime = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startTime = cal.timeInMillis

        val dateFormat = java.text.SimpleDateFormat("EEEE, d 'de' MMMM", java.util.Locale("es"))
        val dateLabel = dateFormat.format(cal.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }

        val pm = context.packageManager

        if (!hasUsageStatsPermission()) {
            return@withContext DailyDetailStats(
                dateLabel = dateLabel,
                firstUseTime = "--",
                avgSessionMinutes = 0,
                mostUsedAppName = "--",
                mostUsedAppTime = "--",
                unlocks = 0,
                totalTimeMillis = 0L
            )
        }

        val aggregated = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
        val totalTime = aggregated.values.sumOf { it.totalTimeInForeground }

        val topApp = aggregated.values.maxByOrNull { it.totalTimeInForeground }
        val mostUsedName = topApp?.let {
            try {
                val info = pm.getApplicationInfo(it.packageName, 0)
                pm.getApplicationLabel(info).toString()
            } catch (_: Exception) { it.packageName }
        } ?: "--"

        val mostUsedMillis = topApp?.totalTimeInForeground ?: 0L
        val mostUsedH = (mostUsedMillis / 3_600_000).toInt()
        val mostUsedM = ((mostUsedMillis % 3_600_000) / 60_000).toInt()
        val mostUsedTimeStr = if (mostUsedH > 0) "${mostUsedH}h ${mostUsedM}m" else "${mostUsedM}m"

        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()

        var firstEventTime = Long.MAX_VALUE
        var sessionCount = 0
        var lastActivityResumedTime = 0L

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED) {
                val t = event.timeStamp
                if (t < firstEventTime) firstEventTime = t
                if (t - lastActivityResumedTime > 5 * 60_000) sessionCount++
                lastActivityResumedTime = t
            }
        }

        val firstUseStr = if (firstEventTime == Long.MAX_VALUE) {
            "--"
        } else {
            java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                .format(java.util.Date(firstEventTime))
        }

        val avgSessionMins = if (sessionCount > 0 && totalTime > 0) {
            ((totalTime / sessionCount) / 60_000).toInt()
        } else 0

        val unlocks = countDeviceUnlocks(startTime, endTime)

        DailyDetailStats(
            dateLabel = dateLabel,
            firstUseTime = firstUseStr,
            avgSessionMinutes = avgSessionMins,
            mostUsedAppName = mostUsedName,
            mostUsedAppTime = mostUsedTimeStr,
            unlocks = unlocks,
            totalTimeMillis = totalTime
        )
    }

    private fun countDeviceUnlocks(startTime: Long, endTime: Long): Int {
        var count = 0
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()
        var lastEventTime = 0L
        
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == 15 || event.eventType == 18) {
                 if (event.eventType == 15) count++
            } else if (event.eventType == 1) { 
                if (event.timeStamp - lastEventTime > 5 * 60 * 1000) {
                    count++
                }
                lastEventTime = event.timeStamp
            }
        }
        return count
    }

    override suspend fun getHourlyUsage(days: Int): List<Float> = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission() || days <= 0) return@withContext List(24) { 0f }

        val hourlyMillis = LongArray(24) { 0L }
        val cal = Calendar.getInstance()
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -days)
        val startTime = cal.timeInMillis

        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()
        
        var currentResumedTime = -1L
        
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            
            when (event.eventType) {
                android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED -> {
                    currentResumedTime = event.timeStamp
                }
                android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED,
                android.app.usage.UsageEvents.Event.ACTIVITY_STOPPED -> {
                    if (currentResumedTime != -1L) {
                        val pauseTime = event.timeStamp
                        val duration = pauseTime - currentResumedTime
                        if (duration > 0) {
                            distributeDurationToHours(currentResumedTime, pauseTime, hourlyMillis)
                        }
                        currentResumedTime = -1L
                    }
                }
            }
        }
        
        if (currentResumedTime != -1L && currentResumedTime < endTime) {
            distributeDurationToHours(currentResumedTime, endTime, hourlyMillis)
        }

        hourlyMillis.map { (it / (60_000f * days)) }
    }

    private fun distributeDurationToHours(startMillis: Long, endMillis: Long, hourlyBuckets: LongArray) {
        val tempCal = Calendar.getInstance()
        var current = startMillis
        
        while (current < endMillis) {
            tempCal.timeInMillis = current
            val hour = tempCal.get(Calendar.HOUR_OF_DAY)
            
            tempCal.set(Calendar.MINUTE, 0)
            tempCal.set(Calendar.SECOND, 0)
            tempCal.set(Calendar.MILLISECOND, 0)
            tempCal.add(Calendar.HOUR_OF_DAY, 1)
            val nextHourBoundary = tempCal.timeInMillis
            
            val chunkEnd = minOf(nextHourBoundary, endMillis)
            val durationInThisHour = chunkEnd - current
            
            hourlyBuckets[hour] += durationInThisHour
            current = chunkEnd
        }
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

}
