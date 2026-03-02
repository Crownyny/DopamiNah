package co.edu.unicauca.dopaminah.utils

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
        val absDiffMinute = Math.abs(diff) / (1000 * 60)
        val hours = absDiffMinute / 60
        val minutes = absDiffMinute % 60
        val timeString = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        
        return when {
             diff > 0 -> "↗ +$timeString vs ayer"
             diff < 0 -> "↘ -$timeString vs ayer"
             else -> "= Igual que ayer"
        }
    }

    fun formatUsageTime(timeInMillis: Long): String {
        val totalMinutes = timeInMillis / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }
}
