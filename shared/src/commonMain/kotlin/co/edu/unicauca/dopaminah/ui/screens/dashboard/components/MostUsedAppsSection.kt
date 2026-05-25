package co.edu.unicauca.dopaminah.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.ui.icons.LucideStar
import co.edu.unicauca.dopaminah.ui.icons.LucideSearch

private enum class SortOrder { MOST_USED, LEAST_USED }

@Composable
fun MostUsedAppsSection(
    dailyUsageStats: List<AppUsageSummary>,
    hasPermission: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(SortOrder.MOST_USED) }
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) { Icon(LucideStar, contentDescription = null, tint = colorScheme.onPrimary, modifier = Modifier.size(20.dp)) }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Apps Más Usadas Hoy", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar app...", color = colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(LucideSearch, contentDescription = "Buscar", tint = colorScheme.primary) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = colorScheme.primaryContainer, focusedBorderColor = colorScheme.primary, unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent, focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { sortOrder = SortOrder.MOST_USED },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (sortOrder == SortOrder.MOST_USED) colorScheme.primary else colorScheme.surfaceVariant,
                        contentColor = if (sortOrder == SortOrder.MOST_USED) colorScheme.onPrimary else colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("↓ Más usado", fontWeight = FontWeight.Bold) }
                Button(
                    onClick = { sortOrder = SortOrder.LEAST_USED },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (sortOrder == SortOrder.LEAST_USED) colorScheme.primary else colorScheme.surfaceVariant,
                        contentColor = if (sortOrder == SortOrder.LEAST_USED) colorScheme.onPrimary else colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) { Text("↑ Menos usado", fontWeight = FontWeight.Bold) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (!hasPermission) {
                Text("Acepta los permisos de uso para ver estadísticas de apps", color = colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
            } else if (dailyUsageStats.isEmpty()) {
                Text("No hay datos suficientes", color = colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
            } else {
                val sorted = when (sortOrder) {
                    SortOrder.MOST_USED -> dailyUsageStats.sortedByDescending { it.totalTimeForegroundMillis }
                    SortOrder.LEAST_USED -> dailyUsageStats.sortedBy { it.totalTimeForegroundMillis }
                }
                sorted.filter { it.appName.contains(searchQuery, ignoreCase = true) }.take(10).forEach { usageSummary ->
                    AppUsageItem(usageSummary = usageSummary)
                }
            }
        }
    }
}
