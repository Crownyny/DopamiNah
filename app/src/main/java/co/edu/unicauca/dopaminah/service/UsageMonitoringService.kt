package co.edu.unicauca.dopaminah.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import co.edu.unicauca.dopaminah.domain.repository.UsageMonitoringRepository
import co.edu.unicauca.dopaminah.domain.usecase.CheckUsageLimitsUseCase
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
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var checkUsageLimitsUseCase: CheckUsageLimitsUseCase

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
                checkUsageLimitsUseCase.execute()
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
                                checkUsageLimitsUseCase.execute()
                            }
                        }
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        serviceScope.launch {
                            monitoringRepository.incrementUnlockCount()
                            checkUsageLimitsUseCase.execute()
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(screenOffReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(screenOffReceiver, filter)
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
