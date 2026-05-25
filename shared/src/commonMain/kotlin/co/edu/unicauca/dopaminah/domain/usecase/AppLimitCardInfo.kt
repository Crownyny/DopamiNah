package co.edu.unicauca.dopaminah.domain.usecase

data class AppLimitCardInfo(
    val packageName: String,
    val appName: String,
    val timeUsedMs: Long,
    val timeLimitMs: Long
)
