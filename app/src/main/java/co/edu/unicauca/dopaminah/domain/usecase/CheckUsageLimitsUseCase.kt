package co.edu.unicauca.dopaminah.domain.usecase

import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import co.edu.unicauca.dopaminah.domain.repository.UsageMonitoringRepository
import co.edu.unicauca.dopaminah.utils.NotificationHelper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckUsageLimitsUseCase @Inject constructor(
    private val deviceUsageRepository: DeviceUsageRepository,
    private val goalsRepository: GoalsRepository,
    private val monitoringRepository: UsageMonitoringRepository,
    private val notificationHelper: NotificationHelper
) {
    suspend fun execute() {
        if (!deviceUsageRepository.hasUsageStatsPermission()) return

        val dailyStats = deviceUsageRepository.getDailyUsageStats()
        val allGoals = goalsRepository.getAllGoals().first()
        
        // Check App Limits
        val appLimits = allGoals.filter { it.goalType == "APP_LIMIT" }
        for (goal in appLimits) {
            val appStat = dailyStats.find { it.packageName == goal.packageName }
            
            if (appStat != null) {
                // Check Usage Time
                if (goal.maxTimeMillis > 0 && appStat.totalTimeForegroundMillis > goal.maxTimeMillis) {
                    val alertId = "app_usage_${goal.id}"
                    if (!monitoringRepository.isAlertNotified(alertId)) {
                        notificationHelper.showNotification(
                            NotificationHelper.APP_USAGE_NOTIF_ID + goal.id,
                            "Límite de uso excedido",
                            "Has usado ${appStat.appName} más tiempo del límite diario."
                        )
                        monitoringRepository.markAlertNotified(alertId)
                    }
                }

                // Check Open Count
                if (goal.maxUnlocks > 0 && appStat.unlockCount > goal.maxUnlocks) {
                    val alertId = "app_open_${goal.id}"
                    if (!monitoringRepository.isAlertNotified(alertId)) {
                        notificationHelper.showNotification(
                            NotificationHelper.APP_OPEN_NOTIF_ID + goal.id,
                            "Límite de aperturas excedido",
                            "Has abierto ${appStat.appName} demasiadas veces hoy."
                        )
                        monitoringRepository.markAlertNotified(alertId)
                    }
                }
            }
        }

        // Check Total Screen Time Limit
        val stats = monitoringRepository.getMonitoringStats().first()
        val screenGoal = allGoals.find { it.goalType == "TOTAL_DAILY" }
        screenGoal?.let {
            if (stats.totalScreenTimeMillis > it.maxTimeMillis) {
                val alertId = "screen_time_total"
                if (!monitoringRepository.isAlertNotified(alertId)) {
                    notificationHelper.showNotification(
                        NotificationHelper.SCREEN_TIME_NOTIF_ID,
                        "Límite de tiempo excedido",
                        "Has superado tu límite diario de uso del celular."
                    )
                    monitoringRepository.markAlertNotified(alertId)
                }
            }
        }

        // Check Unlock Limit
        val unlockGoal = allGoals.find { it.goalType == "UNLOCK_LIMIT" }
        unlockGoal?.let {
            if (stats.unlockCount > it.maxUnlocks) {
                val alertId = "unlock_limit_total"
                if (!monitoringRepository.isAlertNotified(alertId)) {
                    notificationHelper.showNotification(
                        NotificationHelper.UNLOCK_COUNT_NOTIF_ID,
                        "Límite de desbloqueos",
                        "Has desbloqueado tu teléfono demasiadas veces hoy."
                    )
                    monitoringRepository.markAlertNotified(alertId)
                }
            }
        }
    }
}
