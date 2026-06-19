package co.edu.unicauca.dopaminah.domain.model

/** Summary of usage for a single app: foreground time, unlock count, and last-used timestamp. */
data class AppUsageSummary(
    val packageName: String,
    val appName: String,
    val totalTimeForegroundMillis: Long,
    val unlockCount: Int,
    val lastTimeUsed: Long,
    val iconBytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AppUsageSummary
        return packageName == other.packageName &&
                appName == other.appName &&
                totalTimeForegroundMillis == other.totalTimeForegroundMillis &&
                unlockCount == other.unlockCount &&
                lastTimeUsed == other.lastTimeUsed &&
                iconBytes.contentEquals(other.iconBytes)
    }

    override fun hashCode(): Int {
        var result = packageName.hashCode()
        result = 31 * result + appName.hashCode()
        result = 31 * result + totalTimeForegroundMillis.hashCode()
        result = 31 * result + unlockCount
        result = 31 * result + lastTimeUsed.hashCode()
        result = 31 * result + (iconBytes?.contentHashCode() ?: 0)
        return result
    }
}
