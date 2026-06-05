package co.edu.unicauca.dopaminah.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.components.AppIcon
import co.edu.unicauca.dopaminah.ui.icons.*
import co.edu.unicauca.dopaminah.ui.screens.focusbrowser.WebNavigationRepository
import co.edu.unicauca.dopaminah.ui.screens.settings.components.*
import co.edu.unicauca.dopaminah.ui.screens.settings.viewmodel.SettingsViewModel
import co.edu.unicauca.dopaminah.ui.theme.DopaminahPurpleDark
import co.edu.unicauca.dopaminah.ui.theme.extendedColors
import co.edu.unicauca.dopaminah.hasOverlayPermission
import co.edu.unicauca.dopaminah.requestOverlayPermission
import co.edu.unicauca.dopaminah.isMonitoringServiceRunning
import co.edu.unicauca.dopaminah.startMonitoringService
import co.edu.unicauca.dopaminah.stopMonitoringService

private const val URL_PRIVACY_POLICY = "https://dopaminah.app/privacy"
private const val URL_HELP_CENTER = "https://dopaminah.app/help"
private const val URL_CONTACT_SUPPORT = "https://dopaminah.app/support"

@Composable
/** Full-screen composable for the settings tab, with toggles for notifications, Pajaro Verde mode, premium, and about sections. */
fun SettingsScreen(
    darkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    onOpenUrl: (String) -> Unit = {},
    viewModel: SettingsViewModel? = null,
    navRepository: WebNavigationRepository? = null
) {
    val vm = viewModel ?: remember { SettingsViewModel() }

    val notificationsEnabled by vm.notificationsEnabled.collectAsState()
    val pajaroVerdeMode by vm.pajaroVerdeMode.collectAsState()
    val isPremium by vm.isPremium.collectAsState()

    val extended = MaterialTheme.extendedColors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderSection()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(Modifier.height(24.dp))
                if (!isPremium) {
                    PremiumCard(onClick = { vm.setPremium(true) })
                } else {
                    PremiumActiveCard()
                }
            }

            item {
                SettingsSection(title = "Apariencia y Notificaciones") {
                    SettingsToggleItem(
                        icon = if (darkMode) LucideMoon else LucideSun,
                        title = "Modo Oscuro",
                        subtitle = "Reduce la fatiga visual",
                        checked = darkMode,
                        onCheckedChange = onDarkModeChange,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    SettingsToggleItem(
                        icon = LucideBell,
                        title = "Notificaciones",
                        subtitle = "Recibe recordatorios y alertas",
                        checked = notificationsEnabled,
                        onCheckedChange = { vm.toggleNotifications(it) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    SettingsToggleItem(
                        icon = LucideZap,
                        title = "Modo P\u00e1jaro Verde \ud83d\udc1c",
                        subtitle = "Presi\u00f3n activa y mensajes insistentes",
                        checked = pajaroVerdeMode,
                        onCheckedChange = { vm.togglePajaroVerdeMode(it) },
                        activeColor = extended.successGreen
                    )
                }
            }

            item {
                SettingsSection(title = "Privacidad y Seguridad") {
                    SettingsNavigationItem(
                        icon = LucideShield,
                        title = "Pol\u00edtica de Privacidad",
                        onClick = { onOpenUrl(URL_PRIVACY_POLICY) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    SettingsNavigationItem(
                        icon = LucideInfo,
                        title = "Permisos de la App",
                        onClick = { onOpenUrl(URL_PRIVACY_POLICY) }
                    )
                }
            }

            item {
                SettingsSection(title = "Enfoque") {
                    SettingsToggleItem(
                        icon = LucideShieldAlert,
                        title = "Modo Enfoque",
                        subtitle = "Bloquea sitios distractores al navegar",
                        checked = navRepository?.isFocusMode ?: false,
                        onCheckedChange = { checked -> navRepository?.isFocusMode = checked }
                    )

                    if (navRepository != null && navRepository.isFocusMode) {
                        navRepository.rules.forEach { rule ->
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rule.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Switch(
                                    checked = rule.isActive,
                                    onCheckedChange = { navRepository.toggleRule(rule.id) }
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        SettingsNavigationItem(
                            icon = LucideInfo,
                            title = "Restaurar lista predeterminada",
                            onClick = { navRepository.resetDefaults() }
                        )
                    }
                }
            }

            item {
                var isServiceRunning by remember { mutableStateOf(isMonitoringServiceRunning()) }
                var hasOverlayPerm by remember { mutableStateOf(hasOverlayPermission()) }

                LaunchedEffect(Unit) {
                    while (true) {
                        isServiceRunning = isMonitoringServiceRunning()
                        hasOverlayPerm = hasOverlayPermission()
                        kotlinx.coroutines.delay(1000L)
                    }
                }

                SettingsSection(title = "Control de Límites (Android)") {
                    SettingsToggleItem(
                        icon = LucideLock,
                        title = "Bloquear Aplicaciones",
                        subtitle = "Cierra apps al superar el límite diario",
                        checked = isServiceRunning,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (hasOverlayPermission()) {
                                    startMonitoringService()
                                    isServiceRunning = true
                                } else {
                                    requestOverlayPermission()
                                }
                            } else {
                                stopMonitoringService()
                                isServiceRunning = false
                            }
                        }
                    )
                    
                    if (!hasOverlayPerm) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        SettingsNavigationItem(
                            icon = LucideShieldAlert,
                            title = "Permiso: Mostrar sobre otras apps",
                            onClick = { requestOverlayPermission() }
                        )
                    }
                }
            }

            item {
                SettingsSection(title = "Soporte") {
                    SettingsNavigationItem(
                        icon = LucideHelpCircle,
                        title = "Centro de Ayuda",
                        onClick = { onOpenUrl(URL_HELP_CENTER) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    SettingsNavigationItem(
                        icon = LucideMail,
                        title = "Contactar Soporte",
                        onClick = { onOpenUrl(URL_CONTACT_SUPPORT) }
                    )
                }
            }

            item {
                AboutSection()
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(DopaminahPurpleDark)
            .padding(bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Configuraci\u00f3n",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Personaliza tu experiencia",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Box(
                Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                AppIcon(size = 84.dp)
            }
        }
    }
}
