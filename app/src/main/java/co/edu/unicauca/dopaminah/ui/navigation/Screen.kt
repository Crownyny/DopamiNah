package co.edu.unicauca.dopaminah.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import co.edu.unicauca.dopaminah.ui.icons.LucideAward
import co.edu.unicauca.dopaminah.ui.icons.LucideChartColumn
import co.edu.unicauca.dopaminah.ui.icons.LucideHouse
import co.edu.unicauca.dopaminah.ui.icons.LucideSettings
import co.edu.unicauca.dopaminah.ui.icons.LucideTarget

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object OnboardingPermission : Screen("onboarding_permission", "Permisos", LucideSettings)
    object Dashboard    : Screen("dashboard",     "Inicio",  LucideHouse)
    object Stats        : Screen("stats",         "Stats",   LucideChartColumn)
    object Goals        : Screen("goals",         "Metas",   LucideTarget)
    object Achievements : Screen("achievements",  "Logros",  LucideAward)
    object Settings     : Screen("settings",      "Ajustes", LucideSettings)
}
