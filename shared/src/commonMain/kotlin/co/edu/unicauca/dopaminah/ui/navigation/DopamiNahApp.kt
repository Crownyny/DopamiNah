package co.edu.unicauca.dopaminah.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import co.edu.unicauca.dopaminah.ui.theme.DopamiNahTheme
import co.edu.unicauca.dopaminah.ui.screens.dashboard.DashboardScreen
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel
import co.edu.unicauca.dopaminah.ui.screens.stats.StatsScreen
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsViewModel
import co.edu.unicauca.dopaminah.ui.screens.goals.GoalsScreen
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalsViewModel
import co.edu.unicauca.dopaminah.ui.screens.achievements.AchievementsScreen
import co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel.AchievementsViewModel
import co.edu.unicauca.dopaminah.ui.screens.settings.SettingsScreen
import co.edu.unicauca.dopaminah.ui.screens.onboarding.OnboardingPermissionScreen
import co.edu.unicauca.dopaminah.ui.icons.LucideHouse
import co.edu.unicauca.dopaminah.ui.icons.LucideChartColumn
import co.edu.unicauca.dopaminah.ui.icons.LucideTarget
import co.edu.unicauca.dopaminah.ui.icons.LucideAward
import co.edu.unicauca.dopaminah.ui.icons.LucideSettings as LucideSettingsIcon

enum class AppTab(val route: String, val title: String) {
    DASHBOARD("dashboard", "Inicio"),
    STATS("stats", "Stats"),
    GOALS("goals", "Metas"),
    ACHIEVEMENTS("achievements", "Logros"),
    SETTINGS("settings", "Ajustes")
}

@Composable
fun DopamiNahApp(
    darkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    dashboardViewModel: DashboardViewModel? = null,
    statsViewModel: StatsViewModel? = null,
    goalsViewModel: GoalsViewModel? = null,
    achievementsViewModel: AchievementsViewModel? = null
) {
    val permissionState = LocalPermissionState.current

    DopamiNahTheme(darkTheme = darkMode) {
        if (!permissionState.hasUsagePermission) {
            OnboardingPermissionScreen(
                onPermissionGranted = {
                    permissionState.onPermissionGranted()
                },
                onRequestUsagePermission = permissionState.onRequestUsagePermission,
                onRequestNotificationPermission = permissionState.onRequestNotificationPermission
            )
        } else {
            MainContent(
                darkMode = darkMode,
                onDarkModeChange = onDarkModeChange,
                dashboardViewModel = dashboardViewModel,
                statsViewModel = statsViewModel,
                goalsViewModel = goalsViewModel,
                achievementsViewModel = achievementsViewModel
            )
        }
    }
}

@Composable
private fun MainContent(
    darkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    dashboardViewModel: DashboardViewModel? = null,
    statsViewModel: StatsViewModel? = null,
    goalsViewModel: GoalsViewModel? = null,
    achievementsViewModel: AchievementsViewModel? = null
) {
    var selectedTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(LucideHouse, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = selectedTab == AppTab.DASHBOARD,
                    onClick = { selectedTab = AppTab.DASHBOARD }
                )
                NavigationBarItem(
                    icon = { Icon(LucideChartColumn, contentDescription = "Stats") },
                    label = { Text("Stats") },
                    selected = selectedTab == AppTab.STATS,
                    onClick = { selectedTab = AppTab.STATS }
                )
                NavigationBarItem(
                    icon = { Icon(LucideTarget, contentDescription = "Metas") },
                    label = { Text("Metas") },
                    selected = selectedTab == AppTab.GOALS,
                    onClick = { selectedTab = AppTab.GOALS }
                )
                NavigationBarItem(
                    icon = { Icon(LucideAward, contentDescription = "Logros") },
                    label = { Text("Logros") },
                    selected = selectedTab == AppTab.ACHIEVEMENTS,
                    onClick = { selectedTab = AppTab.ACHIEVEMENTS }
                )
                NavigationBarItem(
                    icon = { Icon(LucideSettingsIcon, contentDescription = "Ajustes") },
                    label = { Text("Ajustes") },
                    selected = selectedTab == AppTab.SETTINGS,
                    onClick = { selectedTab = AppTab.SETTINGS }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedTab) {
                AppTab.DASHBOARD -> DashboardScreen(viewModel = dashboardViewModel)
                AppTab.STATS -> StatsScreen(viewModel = statsViewModel)
                AppTab.GOALS -> GoalsScreen(viewModel = goalsViewModel)
                AppTab.ACHIEVEMENTS -> AchievementsScreen(viewModel = achievementsViewModel)
                AppTab.SETTINGS -> SettingsScreen(
                    darkMode = darkMode,
                    onDarkModeChange = onDarkModeChange
                )
            }
        }
    }
}
