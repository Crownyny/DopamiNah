package co.edu.unicauca.dopaminah.data.repository

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Process
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.repository.DailyDetailStats
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DeviceUsageRepositoryImpl(
    private val context: Context
) : DeviceUsageRepository {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val iconCache = mutableMapOf<String, ByteArray>()
    private val appNameCache = mutableMapOf<String, String>()

    override suspend fun getDailyUsageStats(): List<AppUsageSummary> = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission()) return@withContext emptyList()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val now = System.currentTimeMillis()

        val packageManager = context.packageManager

        val foregroundTime = mutableMapOf<String, Long>()
        val unlockCounts = mutableMapOf<String, Int>()
        val resumed = mutableMapOf<String, Long>()
        var lastUnlockTime = 0L

        val events = usageStatsManager.queryEvents(startTime, now)
        val ev = android.app.usage.UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(ev)
            when (ev.eventType) {
                android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED -> {
                    resumed[ev.packageName] = ev.timeStamp
                    if (ev.timeStamp - lastUnlockTime > 5 * 60 * 1000) {
                        unlockCounts[ev.packageName] = (unlockCounts[ev.packageName] ?: 0) + 1
                        lastUnlockTime = ev.timeStamp
                    }
                }
                android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED,
                android.app.usage.UsageEvents.Event.ACTIVITY_STOPPED -> {
                    val resumeTime = resumed.remove(ev.packageName) ?: continue
                    val dur = ev.timeStamp - resumeTime
                    if (dur > 0 && dur < 600_000L) {
                        foregroundTime[ev.packageName] = (foregroundTime[ev.packageName] ?: 0L) + dur
                    }
                }
            }
        }

        if (foregroundTime.isNotEmpty()) {
            foregroundTime.filter { it.value > 0 }
                .mapNotNull { (packageName, timeMs) ->
                    if (isSystemService(packageName, packageManager)) return@mapNotNull null
                    val appName = appNameCache.getOrPut(packageName) {
                        try {
                            val info = packageManager.getApplicationInfo(packageName, 0)
                            packageManager.getApplicationLabel(info).toString()
                        } catch (_: Exception) { packageName }
                    }
                    AppUsageSummary(
                        packageName = packageName, appName = appName,
                        totalTimeForegroundMillis = timeMs,
                        unlockCount = unlockCounts[packageName] ?: 0,
                        lastTimeUsed = 0L,
                        iconBytes = loadAppIconBytes(packageName, packageManager)
                    )
                }.sortedByDescending { it.totalTimeForegroundMillis }
        } else {
            val stats = usageStatsManager.queryAndAggregateUsageStats(startTime, startTime + 86_400_000L)
            stats.values.filter { it.totalTimeInForeground > 0 }
                .mapNotNull { usageStat ->
                    val pkg = usageStat.packageName
                    if (isSystemService(pkg, packageManager)) return@mapNotNull null
                    val appName = appNameCache.getOrPut(pkg) {
                        try {
                            val info = packageManager.getApplicationInfo(pkg, 0)
                            packageManager.getApplicationLabel(info).toString()
                        } catch (_: Exception) { pkg }
                    }
                    AppUsageSummary(
                        packageName = pkg, appName = appName,
                        totalTimeForegroundMillis = usageStat.totalTimeInForeground,
                        unlockCount = unlockCounts[pkg] ?: 0,
                        lastTimeUsed = usageStat.lastTimeUsed,
                        iconBytes = loadAppIconBytes(pkg, packageManager)
                    )
                }.sortedByDescending { it.totalTimeForegroundMillis }
        }
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

    override fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override suspend fun getAverageUsageMillis(days: Int): Long = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission() || days <= 0) return@withContext 0L

        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startTime = calendar.timeInMillis

        val aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
        aggregatedStats.values.sumOf { it.totalTimeInForeground } / days
    }

    override suspend fun getAverageUnlocks(days: Int): Int = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission() || days <= 0) return@withContext 0

        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startTime = calendar.timeInMillis

        countDeviceUnlocks(startTime, endTime) / days
    }

    override suspend fun getDailyUsageForLastDays(days: Int): List<Long> = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission() || days <= 0) return@withContext emptyList()

        (days - 1 downTo 0).map { i ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val start = calendar.timeInMillis

            val endCal = calendar.clone() as Calendar
            endCal.add(Calendar.DAY_OF_YEAR, 1)
            val end = endCal.timeInMillis

            val stats = usageStatsManager.queryAndAggregateUsageStats(start, end)
            stats.values.sumOf { it.totalTimeInForeground }
        }
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

        val dateLabel = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es")).format(cal.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        if (!hasUsageStatsPermission()) {
            return@withContext DailyDetailStats(
                dateLabel = dateLabel, firstUseTime = "--", avgSessionMinutes = 0,
                mostUsedAppName = "--", mostUsedAppTime = "--", unlocks = 0, totalTimeMillis = 0L
            )
        }

        val aggregated = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
        val totalTime = aggregated.values.sumOf { it.totalTimeInForeground }
        val topApp = aggregated.values.maxByOrNull { it.totalTimeInForeground }

        val pm = context.packageManager
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

        var firstUseMillis = -1L
        var sessionCount = 0
        var totalSessionMillis = 0L
        var resumedTime = -1L
        var unlockCount = 0
        var lastEventTime = 0L

        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            when (event.eventType) {
                android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED -> {
                    if (firstUseMillis == -1L) firstUseMillis = event.timeStamp
                    resumedTime = event.timeStamp
                    if (event.timeStamp - lastEventTime > 5 * 60 * 1000) unlockCount++
                    lastEventTime = event.timeStamp
                }
                android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED,
                android.app.usage.UsageEvents.Event.ACTIVITY_STOPPED -> {
                    if (resumedTime != -1L) {
                        val duration = event.timeStamp - resumedTime
                        if (duration > 0) {
                            totalSessionMillis += duration
                            sessionCount++
                        }
                        resumedTime = -1L
                    }
                }
            }
        }

        val firstUseTime = if (firstUseMillis != -1L) {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(firstUseMillis))
                .lowercase().replace("am", "AM").replace("pm", "PM")
        } else "--"

        val avgMinutes = if (sessionCount > 0) (totalSessionMillis / sessionCount / 60_000).toInt() else 0

        DailyDetailStats(
            dateLabel = dateLabel, firstUseTime = firstUseTime,
            avgSessionMinutes = avgMinutes,
            mostUsedAppName = mostUsedName, mostUsedAppTime = mostUsedTimeStr,
            unlocks = unlockCount, totalTimeMillis = totalTime
        )
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
                        val duration = event.timeStamp - currentResumedTime
                        if (duration > 0) distributeToHours(currentResumedTime, event.timeStamp, hourlyMillis)
                        currentResumedTime = -1L
                    }
                }
            }
        }
        if (currentResumedTime != -1L && currentResumedTime < endTime) {
            distributeToHours(currentResumedTime, endTime, hourlyMillis)
        }

        hourlyMillis.map { it / (60_000f * days) }
    }

    private fun countDeviceUnlocks(startTime: Long, endTime: Long): Int {
        var count = 0
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val event = android.app.usage.UsageEvents.Event()
        var lastEventTime = 0L
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED) {
                if (event.timeStamp - lastEventTime > 5 * 60 * 1000) count++
                lastEventTime = event.timeStamp
            }
        }
        return count
    }

    private fun distributeToHours(startMillis: Long, endMillis: Long, buckets: LongArray) {
        var current = startMillis
        val tempCal = Calendar.getInstance()
        while (current < endMillis) {
            tempCal.timeInMillis = current
            val hour = tempCal.get(Calendar.HOUR_OF_DAY)
            tempCal.set(Calendar.MINUTE, 0)
            tempCal.set(Calendar.SECOND, 0)
            tempCal.set(Calendar.MILLISECOND, 0)
            tempCal.add(Calendar.HOUR_OF_DAY, 1)
            val nextBoundary = tempCal.timeInMillis
            val chunkEnd = minOf(nextBoundary, endMillis)
            buckets[hour] += chunkEnd - current
            current = chunkEnd
        }
    }

    private fun isSystemService(packageName: String, pm: PackageManager): Boolean {
        if (packageName.startsWith("android.")) return true
        return try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val isSystem = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
            if (!isSystem) return false
            pm.getLaunchIntentForPackage(packageName) == null
        } catch (_: Exception) {
            false
        }
    }

    fun clearCaches() {
        iconCache.clear()
        appNameCache.clear()
    }

    private fun loadAppIconBytes(packageName: String, pm: PackageManager): ByteArray? {
        iconCache[packageName]?.let { return it }
        return try {
            val drawable = pm.getApplicationIcon(packageName)
            val bitmap = when (drawable) {
                is BitmapDrawable -> drawable.bitmap
                else -> {
                    val w = drawable.intrinsicWidth.coerceAtLeast(1)
                    val h = drawable.intrinsicHeight.coerceAtLeast(1)
                    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmp)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    bmp
                }
            }
            val scaled = Bitmap.createScaledBitmap(bitmap, 48, 48, true)
            val stream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val bytes = stream.toByteArray()
            iconCache[packageName] = bytes
            bytes
        } catch (_: Exception) {
            null
        }
    }
}
