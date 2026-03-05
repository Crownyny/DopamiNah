package co.edu.unicauca.dopaminah.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.edu.unicauca.dopaminah.R
import co.edu.unicauca.dopaminah.ui.screens.settings.components.AboutSection
import co.edu.unicauca.dopaminah.ui.screens.settings.components.PremiumActiveCard
import co.edu.unicauca.dopaminah.ui.screens.settings.components.PremiumCard
import co.edu.unicauca.dopaminah.ui.screens.settings.components.SettingsNavigationItem
import co.edu.unicauca.dopaminah.ui.screens.settings.components.SettingsSection
import co.edu.unicauca.dopaminah.ui.screens.settings.components.SettingsToggleItem
import co.edu.unicauca.dopaminah.ui.screens.settings.viewmodel.SettingsViewModel
import co.edu.unicauca.dopaminah.ui.theme.*

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isDarkMode by viewModel.isDarkTheme.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val pajaroVerdeMode by viewModel.pajaroVerdeMode.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()

    val colorScheme = MaterialTheme.colorScheme
    val extended = MaterialTheme.extendedColors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(DopaminahPurpleDark)
                .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.settings_subtitle),
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧠", fontSize = 24.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Premium Card
            item {
                Spacer(Modifier.height(24.dp))
                if (!isPremium) {
                    PremiumCard(onClick = { viewModel.setPremium(true) })
                } else {
                    PremiumActiveCard()
                }
            }

            // Appearance & Notifications
            item {
                SettingsSection(title = stringResource(R.string.settings_section_appearance)) {
                    SettingsToggleItem(
                        icon = if (isDarkMode == true) Icons.Default.Nightlight else Icons.Default.WbSunny,
                        title = stringResource(R.string.settings_dark_mode),
                        subtitle = stringResource(R.string.settings_dark_mode_desc),
                        checked = isDarkMode == true,
                        onCheckedChange = { viewModel.toggleDarkMode(it) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.outlineVariant
                    )
                    SettingsToggleItem(
                        icon = if (notificationsEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                        title = stringResource(R.string.settings_notifications),
                        subtitle = stringResource(R.string.settings_notifications_desc),
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.outlineVariant
                    )
                    SettingsToggleItem(
                        icon = Icons.Default.Bolt,
                        title = stringResource(R.string.settings_pajaro_verde),
                        subtitle = stringResource(R.string.settings_pajaro_verde_desc),
                        checked = pajaroVerdeMode,
                        onCheckedChange = { viewModel.togglePajaroVerdeMode(it) },
                        activeColor = extended.successGreen
                    )
                }
            }

            // Privacy & Security
            item {
                SettingsSection(title = stringResource(R.string.settings_section_privacy)) {
                    SettingsNavigationItem(
                        icon = Icons.Default.Shield,
                        title = stringResource(R.string.settings_privacy_policy),
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.outlineVariant
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.settings_app_permissions),
                    )
                }
            }

            // Support
            item {
                SettingsSection(title = stringResource(R.string.settings_section_support)) {
                    SettingsNavigationItem(
                        icon = Icons.Default.Help,
                        title = stringResource(R.string.settings_help_center),
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.outlineVariant
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Email,
                        title = stringResource(R.string.settings_contact_support),
                    )
                }
            }

            // About
            item {
                AboutSection()
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
