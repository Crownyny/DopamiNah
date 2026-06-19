package co.edu.unicauca.dopaminah.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/** Shared permission state used during onboarding, provided via [LocalPermissionState]. */
class PermissionState(
    val hasUsagePermission: Boolean = false,
    val onRequestUsagePermission: () -> Unit = {},
    val onRequestNotificationPermission: () -> Unit = {},
    val onPermissionGranted: () -> Unit = {}
)

/** CompositionLocal key for accessing [PermissionState] throughout the composable tree. */
val LocalPermissionState = staticCompositionLocalOf { PermissionState() }
