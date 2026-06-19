package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Use case that combines goals and live usage data to produce app-limit card info for the dashboard. */
class GetDashboardDataUseCase(
    private val deviceUsageRepository: DeviceUsageRepository,
    private val goalsRepository: GoalsRepository
) {
    fun getAppLimitCards(dailyUsageStats: Flow<List<AppUsageSummary>>): Flow<List<AppLimitCardInfo>> {
        return combine(goalsRepository.getAllGoals(), dailyUsageStats) { goals, stats ->
            val appLimits = goals.filter { it.goalType == "APP_LIMIT" }
            appLimits.map { goal ->
                val stat = stats.find { it.packageName == goal.packageName }
                AppLimitCardInfo(
                    packageName = goal.packageName,
                    appName = goal.appDisplayName.ifEmpty { goal.packageName },
                    timeUsedMs = stat?.totalTimeForegroundMillis ?: 0L,
                    timeLimitMs = goal.maxTimeMillis,
                    iconBytes = stat?.iconBytes
                )
            }
        }
    }
}
