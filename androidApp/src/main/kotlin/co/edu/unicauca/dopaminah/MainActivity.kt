package co.edu.unicauca.dopaminah

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.edu.unicauca.dopaminah.data.repository.DeviceUsageRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.GamificationRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.GoalsRepositoryImpl
import co.edu.unicauca.dopaminah.domain.usecase.UpdateStreakUseCase
import co.edu.unicauca.dopaminah.ui.navigation.PermissionState
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel
import co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel.AchievementsViewModel
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalsViewModel
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsViewModel

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val deviceRepo = DeviceUsageRepositoryImpl(applicationContext)
        val gamificationRepo = GamificationRepositoryImpl(DevicePreferences(applicationContext))
        val updateStreakUseCase = UpdateStreakUseCase(gamificationRepo)

        setContent {
            var permissionGranted by remember {
                mutableStateOf(hasUsageStatsPermission(this@MainActivity))
            }

            DisposableEffect(this@MainActivity) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        permissionGranted = hasUsageStatsPermission(this@MainActivity)
                    }
                }
                lifecycle.addObserver(observer)
                onDispose { lifecycle.removeObserver(observer) }
            }

            val permissionState = remember(permissionGranted) {
                PermissionState(
                    hasUsagePermission = permissionGranted,
                    onRequestUsagePermission = {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    },
                    onRequestNotificationPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(
                                android.Manifest.permission.POST_NOTIFICATIONS
                            )
                        }
                    },
                    onPermissionGranted = {
                        permissionGranted = true
                    }
                )
            }

            val dashboardViewModel = remember(permissionGranted) {
                if (permissionGranted) {
                    DashboardViewModel(
                        gamificationRepository = gamificationRepo,
                        deviceUsageRepository = deviceRepo,
                        updateStreakUseCase = updateStreakUseCase
                    )
                } else {
                    DashboardViewModel.createEmpty()
                }
            }

            val statsViewModel = remember(permissionGranted) {
                if (permissionGranted) {
                    StatsViewModel(repository = deviceRepo)
                } else {
                    StatsViewModel()
                }
            }

            val goalsRepo = remember { GoalsRepositoryImpl(applicationContext) }
            val installedApps = remember { getInstalledApps(this@MainActivity) }
            val goalsViewModel = remember(goalsRepo) {
                GoalsViewModel(
                    goalsRepository = goalsRepo,
                    deviceUsageRepository = deviceRepo,
                    installedApps = installedApps
                )
            }

            val achievementsViewModel = remember(gamificationRepo) {
                AchievementsViewModel(gamificationRepository = gamificationRepo)
            }

            App(
                permissionState = permissionState,
                dashboardViewModel = dashboardViewModel,
                statsViewModel = statsViewModel,
                goalsViewModel = goalsViewModel,
                achievementsViewModel = achievementsViewModel
            )
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE)
                as android.app.AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    private fun getInstalledApps(context: Context): Map<String, String> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolvedInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        return resolvedInfos
            .associate { it.loadLabel(pm).toString() to it.activityInfo.packageName }
            .toSortedMap(String.CASE_INSENSITIVE_ORDER)
    }
}
