package co.edu.unicauca.dopaminah

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import co.edu.unicauca.dopaminah.data.db.DatabaseDriverFactory
import co.edu.unicauca.dopaminah.data.db.DopamiNahDb
import co.edu.unicauca.dopaminah.data.repository.GoalsRepositoryImpl
import co.edu.unicauca.dopaminah.ui.theme.DopamiNahTheme
import co.edu.unicauca.dopaminah.ui.theme.DopaminahOrange
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurple
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import android.util.Log

class AppLimitMonitoringService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
    private var isRunningLoop = false

    private var overlayView: ComposeView? = null
    private var lifecycleOwner: ServiceLifecycleOwner? = null
    private var currentBlockedPackage: String? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bienestar Digital Activo")
            .setContentText("Supervisando tus límites diarios de uso")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, builder.build(), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, builder.build())
        }

        setServiceRunning(true)
        startMonitoringLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningLoop = false
        serviceJob.cancel()
        
        serviceScope.launch(Dispatchers.Main) {
            removeBlockOverlay()
        }
        
        setServiceRunning(false)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startMonitoringLoop() {
        if (isRunningLoop) return
        isRunningLoop = true

        serviceScope.launch {
            val db = DopamiNahDb(DatabaseDriverFactory(applicationContext).createDriver())
            val goalsRepo = GoalsRepositoryImpl(db)

            while (isRunningLoop) {
                try {
                    delay(1500L)

                    // 1. Get current foreground app
                    val foregroundApp = getForegroundPackageName()

                    if (foregroundApp == null || foregroundApp == packageName) {
                        if (overlayView != null) {
                            withContext(Dispatchers.Main) {
                                removeBlockOverlay()
                            }
                        }
                        continue
                    }

                    if (overlayView != null && foregroundApp != currentBlockedPackage) {
                        withContext(Dispatchers.Main) {
                            removeBlockOverlay()
                        }
                    }

                    if (isAppBypassed(foregroundApp)) continue

                    // 2a. Focus mode blocking — block known distraction apps immediately
                    if (isFocusModeBlockingEnabled() && foregroundApp in getFocusBlockedPackages()) {
                        if (hasOverlayPermission()) {
                            withContext(Dispatchers.Main) {
                                showBlockOverlay(
                                    packageName = foregroundApp,
                                    appName = getAppName(foregroundApp),
                                    limitMinutes = null,
                                    isFocusBlock = true
                                )
                            }
                            continue
                        }
                        Log.w(TAG, "Overlay permission denied — focus-block skipped, falling through to time-limit check for $foregroundApp")
                    }

                    // 2b. Fetch limit goals
                    val goals = goalsRepo.getAllGoals().first()
                    val appGoal = goals.find { it.packageName == foregroundApp && it.goalType == "APP_LIMIT" } ?: continue

                    if (appGoal.maxTimeMillis <= 0) continue

                    // 3. Get today's usage for this app
                    val todayUsageMs = getTodayUsageMillis(foregroundApp)

                    // 4. Check if limit is exceeded and not bypassed
                    if (todayUsageMs >= appGoal.maxTimeMillis) {
                        if (!hasOverlayPermission()) {
                            Log.w(TAG, "Overlay permission denied — cannot block $foregroundApp")
                            continue
                        }
                        withContext(Dispatchers.Main) {
                            showBlockOverlay(
                                packageName = foregroundApp,
                                appName = appGoal.appDisplayName,
                                limitMinutes = (appGoal.maxTimeMillis / 60000).toInt(),
                                isFocusBlock = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Monitoring loop error", e)
                }
            }
        }
    }

    private fun getForegroundPackageName(): String? {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(now - 30_000L, now)
        val event = android.app.usage.UsageEvents.Event()
        var lastResumedPackage: String? = null
        var lastResumedTime = 0L

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED) {
                if (event.timeStamp >= lastResumedTime || lastResumedPackage == null) {
                    lastResumedPackage = event.packageName
                    lastResumedTime = event.timeStamp
                }
            }
        }
        return lastResumedPackage
    }

    private fun getTodayUsageMillis(packageName: String): Long {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        var totalMs = 0L
        val resumed = mutableMapOf<String, Long>()
        var hasAnyEvents = false
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val ev = android.app.usage.UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(ev)
            hasAnyEvents = true
            when (ev.eventType) {
                android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED -> {
                    resumed[ev.packageName] = ev.timeStamp
                }
                android.app.usage.UsageEvents.Event.ACTIVITY_PAUSED,
                android.app.usage.UsageEvents.Event.ACTIVITY_STOPPED -> {
                    val resumeTime = resumed.remove(ev.packageName) ?: continue
                    val dur = ev.timeStamp - resumeTime
                    if (dur > 0 && dur < 600_000L && ev.packageName == packageName) {
                        totalMs += dur
                    }
                }
            }
        }

        if (!hasAnyEvents) {
            val stats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
            return stats[packageName]?.totalTimeInForeground ?: 0L
        }

        val currentResume = resumed[packageName]
        if (currentResume != null) {
            val dur = endTime - currentResume
            if (dur > 0 && dur < 600_000L) {
                totalMs += dur
            }
        }

        return totalMs
    }

    private fun getAppName(packageName: String): String {
        return try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            packageName
        }
    }

    private fun showBlockOverlay(packageName: String, appName: String, limitMinutes: Int?, isFocusBlock: Boolean) {
        if (overlayView != null) return

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val lifecycleOwner = ServiceLifecycleOwner()
        this.lifecycleOwner = lifecycleOwner

        val composeView = ComposeView(this).apply {
            setContent {
                DopamiNahTheme {
                    BlockScreenContent(
                        appName = appName,
                        limitMinutes = limitMinutes,
                        isFocusBlock = isFocusBlock,
                        onExit = {
                            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(homeIntent)
                            removeBlockOverlay()
                        },
                        onContinue = {
                            addBypassApp(packageName)
                            removeBlockOverlay()
                        }
                    )
                }
            }
        }

        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(lifecycleOwner)

        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )

        try {
            windowManager.addView(composeView, params)
            overlayView = composeView
            currentBlockedPackage = packageName
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeBlockOverlay() {
        val view = overlayView ?: return
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        lifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        
        try {
            windowManager.removeView(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        overlayView = null
        lifecycleOwner = null
        currentBlockedPackage = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitor de Límites",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Muestra el estado del supervisor de tiempo de uso de aplicaciones"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "AppLimitMonitor"
        private const val CHANNEL_ID = "app_limit_monitor_channel"
        private const val NOTIFICATION_ID = 1005
    }
}

class ServiceLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val mViewModelStore = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = mViewModelStore

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    init {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }
}

@Composable
fun BlockScreenContent(
    appName: String,
    limitMinutes: Int?,
    isFocusBlock: Boolean,
    onExit: () -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isFocusBlock) "🎯" else "⚠️",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = if (isFocusBlock) "Modo Enfoque" else "Límite Excedido",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFocusBlock) DopaminahOrange else MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                val description = if (isFocusBlock) {
                    "$appName está bloqueado por el Modo Enfoque."
                } else {
                    "Has superado tu límite diario de $limitMinutes min configurado para $appName."
                }
                Text(
                    text = description,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DopaminahPurple)
                ) {
                    Text(
                        text = "Salir de la app",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DopaminahOrange)
                ) {
                    Text(
                        text = "Continuar de todos modos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
