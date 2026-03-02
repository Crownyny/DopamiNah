package co.edu.unicauca.dopaminah.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val themeController = LocalThemeController.current
    val isDarkMode by themeController.isDarkTheme.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    // Local state for UI demonstration (should be moved to ViewModel/Repository later)
    var notificationsEnabled by remember { mutableStateOf(true) }
    var pajaroVerdeMode by remember { mutableStateOf(false) }
    var isPremium by remember { mutableStateOf(false) }

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
                        text = "Configuración",
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Premium Card
            item {
                if (!isPremium) {
                    PremiumCard(onClick = { isPremium = true })
                } else {
                    PremiumActiveCard()
                }
            }

            // Appearance & Notifications
            item {
                SettingsSection(title = "Apariencia y Notificaciones") {
                    SettingsToggleItem(
                        icon = if (isDarkMode == true) Icons.Default.Nightlight else Icons.Default.WbSunny,
                        title = "Modo Oscuro",
                        subtitle = "Reduce la fatiga visual",
                        checked = isDarkMode == true,
                        onCheckedChange = { scope.launch { themeController.setDarkMode(it) } },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.outlineVariant
                    )
                    SettingsToggleItem(
                        icon = if (notificationsEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                        title = "Notificaciones",
                        subtitle = "Recibe recordatorios y alertas",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.outlineVariant
                    )
                    SettingsToggleItem(
                        icon = Icons.Default.Bolt,
                        title = "Modo Pájaro Verde 🦜",
                        subtitle = "Presión activa y mensajes insistentes",
                        checked = pajaroVerdeMode,
                        onCheckedChange = { pajaroVerdeMode = it },
                        activeColor = extended.successGreen
                    )
                }
            }

            // Privacy & Security
            item {
                SettingsSection(title = "Privacidad y Seguridad") {
                    SettingsNavigationItem(
                        icon = Icons.Default.Shield,
                        title = "Política de Privacidad",
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.outlineVariant
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Info,
                        title = "Permisos de la App",
                    )
                }
            }

            // Support
            item {
                SettingsSection(title = "Soporte") {
                    SettingsNavigationItem(
                        icon = Icons.Default.Help,
                        title = "Centro de Ayuda",
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = colorScheme.outlineVariant
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Email,
                        title = "Contactar Soporte",
                    )
                }
            }

            // About
            item {
                AboutSection()
            }
        }
    }
}

@Composable
fun PremiumCard(onClick: () -> Unit) {
    val extended = MaterialTheme.extendedColors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = extended.brandOrange),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Premium", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text("Desbloquea todas las funciones", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$9.99", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Pago único", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }

            Spacer(Modifier.height(16.dp))

            val perks = listOf(
                "✨" to "Estadísticas avanzadas por hora",
                "🎯" to "Metas personalizadas ilimitadas",
                "📊" to "Análisis de comportamiento detallado",
                "📤" to "Exportación de datos",
                "🚫" to "Sin publicidad"
            )

            perks.forEach { (emoji, text) ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(emoji, modifier = Modifier.padding(end = 8.dp))
                    Text(text, fontSize = 14.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = extended.brandOrange),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text("Desbloquear Ahora", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PremiumActiveCard() {
    val extended = MaterialTheme.extendedColors
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = extended.brandOrange),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Text("Eres Premium ✨", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (checked) activeColor else colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
                Text(subtitle, fontSize = 14.sp, color = colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = activeColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = colorScheme.outlineVariant
            )
        )
    }
}

@Composable
fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate */ }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
        }
        Text("→", color = colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AboutSection() {
    val colorScheme = MaterialTheme.colorScheme
    val extended = MaterialTheme.extendedColors
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = extended.aboutSurface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, extended.aboutBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("🧠", fontSize = 32.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text("DopamiNah", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text("Versión 1.0.0", fontSize = 14.sp, color = colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(
                "Recupera tu tiempo y vence la procrastinación digital",
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
