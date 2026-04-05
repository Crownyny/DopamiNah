package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class AppLimitCardInfo(
    val packageName: String,
    val appName: String,
    val timeUsedMs: Long,
    val timeLimitMs: Long
)

class GetDashboardDataUseCase @Inject constructor(
    private val deviceUsageRepository: DeviceUsageRepository,
    private val goalsRepository: GoalsRepository
) {
    fun getAppLimitCards(dailyUsageStats: Flow<List<AppUsageSummary>>): Flow<List<AppLimitCardInfo>> {
        return combine(goalsRepository.getAllGoals(), dailyUsageStats) { goals, stats ->
            val appLimits = goals.filter { it.goalType == "APP_LIMIT" }
            appLimits.map { goal ->
                val usedMs = stats.find { it.packageName == goal.packageName }?.totalTimeForegroundMillis ?: 0L
                AppLimitCardInfo(
                    packageName = goal.packageName,
                    appName = goal.appDisplayName.ifEmpty { goal.packageName },
                    timeUsedMs = usedMs,
                    timeLimitMs = goal.maxTimeMillis
                )
            }
        }
    }
}
