package co.edu.unicauca.dopaminah.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import co.edu.unicauca.dopaminah.domain.repository.UsageMonitoringRepository
import co.edu.unicauca.dopaminah.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class UsageAnalysisWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val deviceUsageRepository: DeviceUsageRepository,
    private val goalsRepository: GoalsRepository,
    private val monitoringRepository: UsageMonitoringRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!deviceUsageRepository.hasUsageStatsPermission()) {
            return Result.failure()
        }

        val dailyStats = deviceUsageRepository.getDailyUsageStats()
        val allGoals = goalsRepository.getAllGoals().first()
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

        return Result.success()
    }
}
