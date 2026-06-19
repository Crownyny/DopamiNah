package co.edu.unicauca.dopaminah.utils

import co.edu.unicauca.dopaminah.currentTimeMillis

/** Day-of-week helpers for generating short/full day labels for the last N days. */
object DateUtils {

    private const val DAY_MS = 86_400_000L
    private const val EPOCH_DAY_ZERO_DAY_OF_WEEK = 4 // 1970-01-01 was Thursday

    private val SHORT_NAMES = listOf("jue", "vie", "sáb", "dom", "lun", "mar", "mié")
    private val FULL_NAMES = listOf("jueves", "viernes", "sábado", "domingo", "lunes", "martes", "miércoles")

    private fun daysSinceEpoch(): Long = currentTimeMillis() / DAY_MS

    fun dayOfWeekShort(daysAgo: Int): String {
        val day = ((daysSinceEpoch() - daysAgo) % 7 + 7) % 7
        return SHORT_NAMES[day.toInt()]
    }

    fun dayOfWeekFull(daysAgo: Int): String {
        val day = ((daysSinceEpoch() - daysAgo) % 7 + 7) % 7
        return FULL_NAMES[day.toInt()]
    }

    fun last7ShortLabels(): List<String> = (6 downTo 0).map { dayOfWeekShort(it) }

    fun last7FullLabels(): List<String> = (6 downTo 0).map { dayOfWeekFull(it) }
}
