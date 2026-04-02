package co.edu.unicauca.dopaminah.service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.*
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.worker.UsageAnalysisWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageMonitoringManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deviceUsageRepository: DeviceUsageRepository
) {

    fun startMonitoring() {
        if (!deviceUsageRepository.hasUsageStatsPermission()) return

        // Start Foreground Service
        val serviceIntent = Intent(context, UsageMonitoringService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // Schedule periodic analysis worker
        scheduleAnalysisWorker()
    }

    private fun scheduleAnalysisWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<UsageAnalysisWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "UsageAnalysisWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    fun stopMonitoring() {
        val serviceIntent = Intent(context, UsageMonitoringService::class.java)
        context.stopService(serviceIntent)
        WorkManager.getInstance(context).cancelUniqueWork("UsageAnalysisWorker")
    }
}
