package co.edu.unicauca.dopaminah.domain.usecase

/** Summary of an app-limit goal's usage vs. limit, used in the dashboard's app-limit cards. */
data class AppLimitCardInfo(
    val packageName: String,
    val appName: String,
    val timeUsedMs: Long,
    val timeLimitMs: Long
)
