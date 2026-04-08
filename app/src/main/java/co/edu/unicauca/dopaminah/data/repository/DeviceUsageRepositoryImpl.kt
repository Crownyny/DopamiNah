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

    /**
     * Manager service providing access to device usage history and statistics.
     * * This service is used to:
     * - queryAndAggregateUsageStats: Retrieve total foreground time per application 
     * over a specific period. This method is generally more reliable for cumulative 
     * data than querying individual INTERVAL_DAILY buckets.
     * - queryEvents: Iterate through raw system events to track specific interactions, 
     * such as counting device unlocks by filtering for event type 15 (SCREEN_INTERACTIVE) 
     * or constructing detailed usage timelines.
     */
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

        // queryAndAggregateUsageStats is the most reliable API:
        // it aggregates ALL events in the given time range, not just pre-computed daily/weekly buckets.
        // INTERVAL_DAILY and INTERVAL_BEST can be stale or empty for the current day.
        val aggregatedStats: Map<String, UsageStats> = usageStatsManager
            .queryAndAggregateUsageStats(startTime, endTime)

        // Build unlock counts from a single events query (efficient: one query for all apps)
        val unlockCounts = mutableMapOf<String, Int>()
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED) {
                unlockCounts[event.packageName] = (unlockCounts[event.packageName] ?: 0) + 1
            }
        }

        // Resolve app names only once per list to keep it efficient
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

        /**
     * Retrieves detailed usage statistics for a specific day based on an offset.
     * * Process flow:
     * 1. Time Calculation: Defines the 24-hour window (midnight to midnight) for the target day.
     * 2. Permission Check: Validates 'Usage Stats' access; returns empty stats if denied.
     * 3. Aggregation: Uses [queryAndAggregateUsageStats] for total foreground time and 
     * identifying the most used application.
     * 4. Event Analysis: Iterates through [UsageEvents] to determine:
     * - First use of the day (first ACTIVITY_RESUMED event).
     * - Session counting (increments if a gap > 5 minutes exists between activities).
     * - Screen interactions (Event type 15).
     * 5. Data Mapping: Packages results into a [DailyDetailStats] object for the UI.
     *
     * @param dayOffset Number of days back from today (0 for today, 1 for yesterday, etc.)
     * @return A [DailyDetailStats] object containing formatted labels, times, and counts.
     */
    override fun getDailyDetails(dayOffset: Int): DailyDetailStats = withContext(Dispatchers.IO) {
        // Setting up the time range for the selected day
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -dayOffset)
        
        // Set to end of day (23:59:59.999)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endTime = cal.timeInMillis

        // Set to start of day (00:00:00.000)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startTime = cal.timeInMillis

        // Format the date label for Spanish locale (e.g., "Viernes, 7 de marzo")
        val dateFormat = java.text.SimpleDateFormat("EEEE, d 'de' MMMM", java.util.Locale("es"))
        val dateLabel = dateFormat.format(cal.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }

        val pm = context.packageManager

        // Safety check: Avoid crash or empty queries if permission is missing
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

        // Retrieve aggregated stats to calculate total time and the "Top App"
        val aggregated = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
        val totalTime = aggregated.values.sumOf { it.totalTimeInForeground }

        // Identify and resolve the name of the most used application
        val topApp = aggregated.values.maxByOrNull { it.totalTimeInForeground }
        val mostUsedName = topApp?.let {
            try {
                val info = pm.getApplicationInfo(it.packageName, 0)
                pm.getApplicationLabel(info).toString()
            } catch (_: Exception) { it.packageName }
        } ?: "--"

        // Format most used app time (e.g., "1h 30m" or "45m")
        val mostUsedMillis = topApp?.totalTimeInForeground ?: 0L
        val mostUsedH = (mostUsedMillis / 3_600_000).toInt()
        val mostUsedM = ((mostUsedMillis % 3_600_000) / 60_000).toInt()
        val mostUsedTimeStr = if (mostUsedH > 0) "${mostUsedH}h ${mostUsedM}m" else "${mostUsedM}m"

        // Raw Event Processing for granular data (First Use and Sessions)
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()

        var firstEventTime = Long.MAX_VALUE
        var sessionCount = 0
        var lastActivityResumedTime = 0L

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            when (event.eventType) {
                android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED -> {
                    val t = event.timeStamp
                    // Capture the very first activity of the day
                    if (t < firstEventTime) firstEventTime = t
                    
                    // Heuristic: If there's a 5-minute gap since the last activity, 
                    // count it as a new "session".
                    if (t - lastActivityResumedTime > 5 * 60_000) sessionCount++
                    lastActivityResumedTime = t
                }
                15 -> { /* SCREEN_INTERACTIVE handled here if needed */ }
            }
        }

        // Format the first interaction time (e.g., "8:30 AM")
        val firstUseStr = if (firstEventTime == Long.MAX_VALUE) {
            "--"
        } else {
            java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                .format(java.util.Date(firstEventTime))
        }

        // Calculate average session duration
        val avgSessionMins = if (sessionCount > 0 && totalTime > 0) {
            ((totalTime / sessionCount) / 60_000).toInt()
        } else 0

        // Fetch unlock count from helper method
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
        
        // Final distribution for any pending resumed activity (capped at endTime)
        if (currentResumedTime != -1L && currentResumedTime < endTime) {
            distributeDurationToHours(currentResumedTime, endTime, hourlyMillis)
        }

        // Convert to minutes and average by days
        hourlyMillis.map { (it / (60_000f * days)) }
    }

    /**
     * Helper to distribute a time range across hour-of-day buckets.
     */
    private fun distributeDurationToHours(startMillis: Long, endMillis: Long, hourlyBuckets: LongArray) {
        val startCal = Calendar.getInstance().apply { timeInMillis = startMillis }
        val endCal = Calendar.getInstance().apply { timeInMillis = endMillis }
        
        var current = startMillis
        val tempCal = Calendar.getInstance()
        
        while (current < endMillis) {
            tempCal.timeInMillis = current
            val hour = tempCal.get(Calendar.HOUR_OF_DAY)
            
            // Calculate next hour boundary
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

    /**
     * Checks if the application has the "Usage Stats" permission (PACKAGE_USAGE_STATS).
     * * This is a special permission that cannot be granted via a standard runtime 
     * dialog and must be enabled by the user in System Settings.
     *
     * @return True if the permission is granted (MODE_ALLOWED), false otherwise.
     * * Note: Uses [AppOpsManager] to check the operational mode, handling API 
     * differences between Android Q (and newer) and older versions.
     */
    override fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        
        // Check operation status using version-specific methods
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // For API 29+, unsafeCheckOpNoThrow is preferred for standard checks
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            // Fallback for older Android versions
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        
        return mode == AppOpsManager.MODE_ALLOWED
    }

}
