package co.edu.unicauca.dopaminah.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import co.edu.unicauca.dopaminah.domain.repository.UsageMonitoringRepository
import co.edu.unicauca.dopaminah.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UsageMonitoringService : Service() {

    @Inject lateinit var monitoringRepository: UsageMonitoringRepository
    @Inject lateinit var goalsRepository: GoalsRepository
    @Inject lateinit var deviceUsageRepository: DeviceUsageRepository
    @Inject lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var screenOffReceiver: BroadcastReceiver? = null
    private var pollingJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(
            NotificationHelper.SERVICE_NOTIF_ID,
            notificationHelper.getForegroundServiceNotification("Monitoreando actividad del dispositivo...")
        )
        registerScreenReceivers()
        checkDailyReset()
        startAppUsagePolling()
    }

    private fun startAppUsagePolling() {
        pollingJob?.cancel()
        pollingJob = serviceScope.launch {
            while (isActive) {
                checkAppUsageLimits()
                delay(60_000) // Check every minute
            }
        }
    }

    private fun registerScreenReceivers() {
        screenOffReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_ON -> {
                        serviceScope.launch {
                            monitoringRepository.setLastScreenOnTime(System.currentTimeMillis())
                        }
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        serviceScope.launch {
                            val startTime = monitoringRepository.getLastScreenOnTime()
                            if (startTime > 0) {
                                val duration = System.currentTimeMillis() - startTime
                                monitoringRepository.updateScreenTime(duration)
                                checkScreenTimeLimit()
                                checkAppUsageLimits() // Check app limits when screen goes off
                            }
                        }
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        serviceScope.launch {
                            monitoringRepository.incrementUnlockCount()
                            checkUnlockLimit()
                        }
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenOffReceiver, filter)
    }

    private suspend fun checkAppUsageLimits() {
        if (!deviceUsageRepository.hasUsageStatsPermission()) return

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
    }

    private suspend fun checkScreenTimeLimit() {
        val stats = monitoringRepository.getMonitoringStats().first()
        val goals = goalsRepository.getAllGoals().first()
        val screenGoal = goals.find { it.goalType == "TOTAL_DAILY" }
        
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
    }

    private suspend fun checkUnlockLimit() {
        val stats = monitoringRepository.getMonitoringStats().first()
        val goals = goalsRepository.getAllGoals().first()
        val unlockGoal = goals.find { it.goalType == "UNLOCK_LIMIT" }
        
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

    private fun checkDailyReset() {
        serviceScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val stats = monitoringRepository.getMonitoringStats().first()
            if (stats.lastResetDate != today) {
                monitoringRepository.resetDailyStats(today)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        screenOffReceiver?.let { unregisterReceiver(it) }
        pollingJob?.cancel()
        serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
