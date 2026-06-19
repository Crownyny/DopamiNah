package co.edu.unicauca.dopaminah.utils

import kotlin.math.abs

/** Formatting helpers for usage time: diff text vs. yesterday and millis-to-readable-string. */
object UsageTimeUtils {

    fun calculateDiffText(today: Int, yesterday: Int): String {
        val diff = today - yesterday
        return when {
            diff > 0 -> "↗ +$diff vs ayer"
            diff < 0 -> "↘ $diff vs ayer"
            else -> "= Igual que ayer"
        }
    }

    fun calculateTimeDiff(today: Long, yesterday: Long): String {
        val diff = today - yesterday
        val absDiffSeconds = abs(diff) / 1000
        val hours = absDiffSeconds / 3600
        val minutes = (absDiffSeconds % 3600) / 60
        val seconds = absDiffSeconds % 60
        val timeString = when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
        return when {
            diff > 0 -> "↗ +$timeString vs ayer"
            diff < 0 -> "↘ -$timeString vs ayer"
            else -> "= Igual que ayer"
        }
    }

    fun formatUsageTime(timeInMillis: Long): String {
        val totalSeconds = timeInMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
}
