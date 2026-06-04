package co.edu.unicauca.dopaminah.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.ui.screens.settings.components.SettingsNavigationItem
import co.edu.unicauca.dopaminah.ui.screens.settings.components.SettingsSection
import co.edu.unicauca.dopaminah.ui.screens.settings.components.SettingsToggleItem
import co.edu.unicauca.dopaminah.ui.screens.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel? = null) {
    val vm = viewModel ?: remember { SettingsViewModel() }

    val darkMode by vm.darkMode.collectAsState()
    val notifications by vm.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            "Configuración",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Personaliza tu experiencia",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))
        SettingsSection("Apariencia y Notificaciones")

        SettingsToggleItem(
            title = "Modo Oscuro",
            description = "Reduce la fatiga visual",
            checked = darkMode,
            onCheckedChange = { vm.toggleDarkMode() }
        )

        SettingsToggleItem(
            title = "Notificaciones",
            description = "Recibe recordatorios y alertas",
            checked = notifications,
            onCheckedChange = { vm.toggleNotifications() }
        )

        Spacer(modifier = Modifier.height(24.dp))
        SettingsSection("Privacidad y Seguridad")

        SettingsNavigationItem(title = "Política de Privacidad")
        SettingsNavigationItem(title = "Permisos de la App")

        Spacer(modifier = Modifier.height(24.dp))
        SettingsSection("Soporte")

        SettingsNavigationItem(title = "Centro de Ayuda")
        SettingsNavigationItem(title = "Contactar Soporte")
    }
}
