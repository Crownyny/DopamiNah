package co.edu.unicauca.dopaminah.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
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
    @Inject lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var screenOffReceiver: BroadcastReceiver? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(
            NotificationHelper.SERVICE_NOTIF_ID,
            notificationHelper.getForegroundServiceNotification("Monitoreando actividad del dispositivo...")
        )
        registerScreenReceivers()
        checkDailyReset()
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

    private suspend fun checkScreenTimeLimit() {
        val stats = monitoringRepository.getMonitoringStats().first()
        val goals = goalsRepository.getAllGoals().first()
        val screenGoal = goals.find { it.goalType == "TOTAL_DAILY" }
        
        screenGoal?.let {
            if (stats.totalScreenTimeMillis > it.maxTimeMillis) {
                notificationHelper.showNotification(
                    NotificationHelper.SCREEN_TIME_NOTIF_ID,
                    "Límite de tiempo excedido",
                    "Has superado tu límite diario de uso del celular."
                )
            }
        }
    }

    private suspend fun checkUnlockLimit() {
        val stats = monitoringRepository.getMonitoringStats().first()
        val goals = goalsRepository.getAllGoals().first()
        val unlockGoal = goals.find { it.goalType == "UNLOCK_LIMIT" }
        
        unlockGoal?.let {
            if (stats.unlockCount > it.maxUnlocks) {
                notificationHelper.showNotification(
                    NotificationHelper.UNLOCK_COUNT_NOTIF_ID,
                    "Límite de desbloqueos",
                    "Has desbloqueado tu teléfono demasiadas veces hoy."
                )
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
        serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
