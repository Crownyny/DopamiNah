package co.edu.unicauca.dopaminah

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.edu.unicauca.dopaminah.ui.navigation.DopamiNahApp
import co.edu.unicauca.dopaminah.ui.navigation.LocalPermissionState
import co.edu.unicauca.dopaminah.ui.navigation.PermissionState
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel
import co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel.AchievementsViewModel
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalsViewModel
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsViewModel

@Composable
fun App(
    permissionState: PermissionState = PermissionState(),
    darkMode: Boolean = false,
    onDarkModeChange: ((Boolean) -> Unit)? = null,
    dashboardViewModel: DashboardViewModel? = null,
    statsViewModel: StatsViewModel? = null,
    goalsViewModel: GoalsViewModel? = null,
    achievementsViewModel: AchievementsViewModel? = null
) {
    var internalDarkMode by remember { mutableStateOf(darkMode) }
    val actualDarkMode = if (onDarkModeChange != null) darkMode else internalDarkMode
    val actualOnChange = onDarkModeChange ?: { internalDarkMode = it }

    CompositionLocalProvider(LocalPermissionState provides permissionState) {
        DopamiNahApp(
            darkMode = actualDarkMode,
            onDarkModeChange = actualOnChange,
            dashboardViewModel = dashboardViewModel,
            statsViewModel = statsViewModel,
            goalsViewModel = goalsViewModel,
            achievementsViewModel = achievementsViewModel
        )
    }
}
