package co.edu.unicauca.dopaminah

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import co.edu.unicauca.dopaminah.ui.navigation.DopamiNahApp
import co.edu.unicauca.dopaminah.ui.navigation.LocalPermissionState
import co.edu.unicauca.dopaminah.ui.navigation.PermissionState
import co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel.DashboardViewModel

@Composable
fun App(
    permissionState: PermissionState = PermissionState(),
    dashboardViewModel: DashboardViewModel? = null
) {
    CompositionLocalProvider(LocalPermissionState provides permissionState) {
        DopamiNahApp(dashboardViewModel = dashboardViewModel)
    }
}
