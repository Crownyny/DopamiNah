package co.edu.unicauca.dopaminah

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil3.compose.setSingletonImageLoaderFactory
import co.edu.unicauca.dopaminah.data.repository.DevicePreferencesWebGoalsRepository
import co.edu.unicauca.dopaminah.domain.repository.WebGoalsRepository
import co.edu.unicauca.dopaminah.ui.navigation.AppTab
import co.edu.unicauca.dopaminah.ui.navigation.DopamiNahApp
import co.edu.unicauca.dopaminah.ui.navigation.LocalPermissionState
import co.edu.unicauca.dopaminah.ui.navigation.PermissionState
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel
import co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel.AchievementsViewModel
import co.edu.unicauca.dopaminah.ui.screens.focusbrowser.WebNavigationRepository
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalsViewModel
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsViewModel

/**
 * Root composable entry point for all platforms.
 *
 * Wraps [DopamiNahApp] with Coil image loader initialization and dark mode state.
 * Each platform entry point (Android MainActivity, Desktop main.kt, Web main.kt, iOS ContentView)
 * calls this composable with the appropriate platform-specific parameters.
 *
 * @param permissionState Tracks overlay and notification permission state
 * @param darkMode Whether dark theme is active (null from parent = managed internally)
 * @param onDarkModeChange Callback when dark mode toggles (null = managed internally)
 * @param dashboardViewModel Injected Android ViewModel or null for default
 * @param statsViewModel Injected Android ViewModel or null for default
 * @param goalsViewModel Injected Android ViewModel or null for default
 * @param achievementsViewModel Injected Android ViewModel or null for default
 * @param navRepository Web navigation state repository
 * @param hiddenTabs Tabs to hide per-platform (e.g. Android hides WEB, Web hides DASHBOARD+STATS)
 * @param useWebGoals Whether to show web goals instead of app goals in the GOALS tab
 * @param onSyncGoalsToExtension Callback to serialize and send goals to browser extension
 * @param webGoalsRepository Repository for web goals persistence (null = in-memory only)
 */
@Composable
fun App(
    permissionState: PermissionState = PermissionState(),
    darkMode: Boolean = false,
    onDarkModeChange: ((Boolean) -> Unit)? = null,
    dashboardViewModel: DashboardViewModel? = null,
    statsViewModel: StatsViewModel? = null,
    goalsViewModel: GoalsViewModel? = null,
    achievementsViewModel: AchievementsViewModel? = null,
    navRepository: WebNavigationRepository? = null,
    hiddenTabs: Set<AppTab> = emptySet(),
    useWebGoals: Boolean = false,
    onSyncGoalsToExtension: ((String) -> Unit)? = null,
    webGoalsRepository: WebGoalsRepository? = null,
    webGoalsPrefs: DevicePreferences? = null
) {
    setSingletonImageLoaderFactory { context ->
        coil3.ImageLoader.Builder(context).build()
    }

    var internalDarkMode by remember { mutableStateOf(darkMode) }
    val actualDarkMode = if (onDarkModeChange != null) darkMode else internalDarkMode
    val actualOnChange = onDarkModeChange ?: { internalDarkMode = it }
    val repository = navRepository ?: remember { WebNavigationRepository() }
    val resolvedWebGoalsRepo = webGoalsRepository ?: webGoalsPrefs?.let { prefs ->
        remember(prefs) { DevicePreferencesWebGoalsRepository(prefs) }
    }

    CompositionLocalProvider(LocalPermissionState provides permissionState) {
        DopamiNahApp(
            darkMode = actualDarkMode,
            onDarkModeChange = actualOnChange,
            dashboardViewModel = dashboardViewModel,
            statsViewModel = statsViewModel,
            goalsViewModel = goalsViewModel,
            achievementsViewModel = achievementsViewModel,
            navRepository = repository,
            hiddenTabs = hiddenTabs,
            useWebGoals = useWebGoals,
            onSyncGoalsToExtension = onSyncGoalsToExtension,
            webGoalsRepository = resolvedWebGoalsRepo
        )
    }
}
