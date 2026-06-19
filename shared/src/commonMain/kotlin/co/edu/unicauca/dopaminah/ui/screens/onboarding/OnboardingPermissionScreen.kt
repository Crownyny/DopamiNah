package co.edu.unicauca.dopaminah.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.ui.screens.onboarding.components.OnboardingActionButton
import co.edu.unicauca.dopaminah.ui.screens.onboarding.components.OnboardingHeader
import co.edu.unicauca.dopaminah.ui.screens.onboarding.components.PermissionPageContent
import kotlinx.coroutines.launch
@Composable
/** Onboarding screen that walks the user through required permissions (usage stats, notifications, overlay) via a horizontal pager. */
fun OnboardingPermissionScreen(
    onPermissionGranted: () -> Unit = {},
    onRequestUsagePermission: () -> Unit = {},
    onRequestNotificationPermission: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingHeader(
                currentPage = pagerState.currentPage,
                totalPages = 3
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    when (page) {
                        0 -> {
                            PermissionPageContent(
                                icon = Icons.Default.Phone,
                                iconTint = Color(0xFF4285F4),
                                title = "Apps Usage",
                                description = "Permítenos monitorear tus aplicaciones para mostrarte estadísticas detalladas de uso."
                            )
                        }
                        1 -> {
                            PermissionPageContent(
                                icon = Icons.Default.Notifications,
                                iconTint = Color(0xFF8B5CF6),
                                title = "Notificaciones",
                                description = "Activa las notificaciones para recibir alertas cuando alcances tus límites."
                            )
                        }
                        2 -> {
                            PermissionPageContent(
                                icon = Icons.Default.Visibility,
                                iconTint = Color(0xFFE91E63),
                                title = "Acceso de Uso",
                                description = "Necesitamos acceso al uso de aplicaciones para ayudarte a establecer límites saludables."
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            OnboardingActionButton(
                text = if (pagerState.currentPage == 2) "Permitir acceso" else "Siguiente",
                onClick = {
                    when (pagerState.currentPage) {
                        0 -> {
                            coroutineScope.launch { pagerState.animateScrollToPage(1) }
                        }
                        1 -> {
                            onRequestNotificationPermission()
                            coroutineScope.launch { pagerState.animateScrollToPage(2) }
                        }
                        2 -> {
                            onRequestUsagePermission()
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
