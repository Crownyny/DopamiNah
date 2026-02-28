package co.edu.unicauca.DopamiNah.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object OnboardingPermission : Screen("onboarding_permission", "Permisos", Icons.Default.Info)
    object Dashboard : Screen("dashboard", "Inicio", Icons.Default.Home)
    object Stats : Screen("stats", "Stats", Icons.Default.List) // Replace with better icon
    object Goals : Screen("goals", "Metas", Icons.Default.Info)
    object Achievements : Screen("achievements", "Logros", Icons.Default.Star)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
}
