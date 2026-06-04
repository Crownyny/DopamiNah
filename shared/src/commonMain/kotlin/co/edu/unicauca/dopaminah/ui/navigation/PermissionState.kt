package co.edu.unicauca.dopaminah.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

class PermissionState(
    val hasUsagePermission: Boolean = false,
    val onRequestUsagePermission: () -> Unit = {},
    val onRequestNotificationPermission: () -> Unit = {},
    val onPermissionGranted: () -> Unit = {}
)

val LocalPermissionState = staticCompositionLocalOf { PermissionState() }
