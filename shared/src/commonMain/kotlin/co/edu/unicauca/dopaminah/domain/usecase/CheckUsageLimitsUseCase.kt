package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import co.edu.unicauca.dopaminah.domain.repository.UsageMonitoringRepository
import kotlinx.coroutines.flow.first

/**
 * Use case that checks all active goals against current usage and triggers notifications
 * when screen-time, app-limit, or unlock-limit thresholds are exceeded.
 */
class CheckUsageLimitsUseCase(
    private val deviceUsageRepository: DeviceUsageRepository,
    private val goalsRepository: GoalsRepository,
    private val monitoringRepository: UsageMonitoringRepository,
    private val onNotify: (id: Int, title: String, message: String) -> Unit
) {
    suspend fun execute() {
        if (!deviceUsageRepository.hasUsageStatsPermission()) return

        val dailyStats = deviceUsageRepository.getDailyUsageStats()
        val allGoals = goalsRepository.getAllGoals().first()

        val appLimits = allGoals.filter { it.goalType == "APP_LIMIT" }
        for (goal in appLimits) {
            val appStat = dailyStats.find { it.packageName == goal.packageName }
            if (appStat != null) {
                if (goal.maxTimeMillis > 0 && appStat.totalTimeForegroundMillis > goal.maxTimeMillis) {
                    val alertId = "app_usage_${goal.id}"
                    if (!monitoringRepository.isAlertNotified(alertId)) {
                        onNotify(1003 + goal.id, "Límite de uso excedido", "Has usado ${appStat.appName} más tiempo del límite diario.")
                        monitoringRepository.markAlertNotified(alertId)
                    }
                }
                if (goal.maxUnlocks > 0 && appStat.unlockCount > goal.maxUnlocks) {
                    val alertId = "app_open_${goal.id}"
                    if (!monitoringRepository.isAlertNotified(alertId)) {
                        onNotify(1004 + goal.id, "Límite de aperturas excedido", "Has abierto ${appStat.appName} demasiadas veces hoy.")
                        monitoringRepository.markAlertNotified(alertId)
                    }
                }
            }
        }

        val stats = monitoringRepository.getMonitoringStats().first()
        val screenGoal = allGoals.find { it.goalType == "TOTAL_DAILY" }
        screenGoal?.let {
            if (stats.totalScreenTimeMillis > it.maxTimeMillis) {
                val alertId = "screen_time_total"
                if (!monitoringRepository.isAlertNotified(alertId)) {
                    onNotify(1001, "Límite de tiempo excedido", "Has superado tu límite diario de uso del celular.")
                    monitoringRepository.markAlertNotified(alertId)
                }
            }
        }

        val unlockGoal = allGoals.find { it.goalType == "UNLOCK_LIMIT" }
        unlockGoal?.let {
            if (stats.unlockCount > it.maxUnlocks) {
                val alertId = "unlock_limit_total"
                if (!monitoringRepository.isAlertNotified(alertId)) {
                    onNotify(1002, "Límite de desbloqueos", "Has desbloqueado tu teléfono demasiadas veces hoy.")
                    monitoringRepository.markAlertNotified(alertId)
                }
            }
        }
    }
}
