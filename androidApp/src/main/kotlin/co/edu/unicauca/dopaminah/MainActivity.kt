package co.edu.unicauca.dopaminah

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import co.edu.unicauca.dopaminah.data.db.DatabaseDriverFactory
import co.edu.unicauca.dopaminah.data.db.DopamiNahDb
import co.edu.unicauca.dopaminah.data.repository.DeviceUsageRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.GamificationRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.GoalsRepositoryImpl
import co.edu.unicauca.dopaminah.data.repository.WebGoalsRepositoryImpl
import co.edu.unicauca.dopaminah.domain.model.AppInfo
import co.edu.unicauca.dopaminah.domain.usecase.GetDashboardDataUseCase
import co.edu.unicauca.dopaminah.domain.usecase.UpdateStreakUseCase
import co.edu.unicauca.dopaminah.ui.navigation.AppTab
import co.edu.unicauca.dopaminah.ui.navigation.PermissionState
import co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel.AchievementsViewModel
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalsViewModel
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsViewModel
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private fun loadInstalledApps(): List<AppInfo> {
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        return resolveInfos
            .map { info ->
                val appName = info.loadLabel(pm).toString()
                val pkg = info.activityInfo.packageName
                val iconBytes = runCatching {
                    val drawable = info.loadIcon(pm)
                    val bitmap = when (drawable) {
                        is BitmapDrawable -> drawable.bitmap
                        else -> {
                            val w = drawable.intrinsicWidth.coerceAtLeast(1)
                            val h = drawable.intrinsicHeight.coerceAtLeast(1)
                            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bmp)
                            drawable.setBounds(0, 0, canvas.width, canvas.height)
                            drawable.draw(canvas)
                            bmp
                        }
                    }
                    val scaled = Bitmap.createScaledBitmap(bitmap, 48, 48, true)
                    val stream = ByteArrayOutputStream()
                    scaled.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.toByteArray()
                }.getOrNull()
                AppInfo(appName, pkg, iconBytes)
            }
            .distinctBy { it.displayName }
            .sortedBy { it.displayName }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setAppContext(applicationContext)

        val db = DopamiNahDb(DatabaseDriverFactory(applicationContext).createDriver())

        val deviceUsageRepo = DeviceUsageRepositoryImpl(applicationContext)
        val gamificationRepo = GamificationRepositoryImpl(db)
        val goalsRepo = GoalsRepositoryImpl(db)
        val webGoalsRepo = WebGoalsRepositoryImpl(db)
        val getDashboardDataUseCase = GetDashboardDataUseCase(deviceUsageRepo, goalsRepo)
        val updateStreakUseCase = UpdateStreakUseCase(gamificationRepo)

        val installedApps = loadInstalledApps()

        setContent {
            val permissionState = remember {
                PermissionState(
                    hasUsagePermission = true,
                    onRequestUsagePermission = {},
                    onRequestNotificationPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(
                                android.Manifest.permission.POST_NOTIFICATIONS
                            )
                        }
                    },
                    onPermissionGranted = {}
                )
            }

            val dashboardViewModel = remember {
                DashboardViewModel(
                    gamificationRepository = gamificationRepo,
                    deviceUsageRepository = deviceUsageRepo,
                    getDashboardDataUseCase = getDashboardDataUseCase,
                    updateStreakUseCase = updateStreakUseCase
                )
            }

            val statsViewModel = remember {
                StatsViewModel(repository = deviceUsageRepo)
            }

            val goalsViewModel = remember {
                GoalsViewModel(
                    goalsRepository = goalsRepo,
                    deviceUsageRepository = deviceUsageRepo,
                    installedApps = installedApps
                )
            }

            val achievementsViewModel = remember {
                AchievementsViewModel(
                    gamificationRepository = gamificationRepo,
                    deviceUsageRepository = deviceUsageRepo,
                    goalsRepository = goalsRepo
                )
            }

            App(
                permissionState = permissionState,
                dashboardViewModel = dashboardViewModel,
                statsViewModel = statsViewModel,
                goalsViewModel = goalsViewModel,
                achievementsViewModel = achievementsViewModel,
                hiddenTabs = setOf(AppTab.WEB),
                webGoalsRepository = webGoalsRepo
            )
        }
    }
}
