package co.edu.unicauca.dopaminah.domain.usecase

/** Summary of an app-limit goal's usage vs. limit, used in the dashboard's app-limit cards. */
data class AppLimitCardInfo(
    val packageName: String,
    val appName: String,
    val timeUsedMs: Long,
    val timeLimitMs: Long,
    val iconBytes: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AppLimitCardInfo
        return packageName == other.packageName &&
                appName == other.appName &&
                timeUsedMs == other.timeUsedMs &&
                timeLimitMs == other.timeLimitMs &&
                iconBytes.contentEquals(other.iconBytes)
    }

    override fun hashCode(): Int {
        var result = packageName.hashCode()
        result = 31 * result + appName.hashCode()
        result = 31 * result + timeUsedMs.hashCode()
        result = 31 * result + timeLimitMs.hashCode()
        result = 31 * result + (iconBytes?.contentHashCode() ?: 0)
        return result
    }
}
