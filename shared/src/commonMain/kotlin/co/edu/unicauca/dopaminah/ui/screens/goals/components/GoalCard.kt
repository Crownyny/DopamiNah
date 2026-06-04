package co.edu.unicauca.dopaminah.ui.screens.goals.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import co.edu.unicauca.dopaminah.ui.theme.extendedColors
import co.edu.unicauca.dopaminah.utils.UsageTimeUtils

@Composable
fun GoalCard(goal: AppLimitGoal) {
    val typeLabel = when (goal.goalType) {
        "TOTAL_DAILY" -> "Tiempo Total Diario"
        "APP_LIMIT" -> "Límite de Aplicación"
        "UNLOCK_LIMIT" -> "Límite Desbloqueos"
        else -> goal.goalType
    }
    val limitText = when (goal.goalType) {
        "TOTAL_DAILY" -> UsageTimeUtils.formatUsageTime(goal.maxTimeMillis)
        "APP_LIMIT" -> "${goal.appDisplayName} - ${UsageTimeUtils.formatUsageTime(goal.maxTimeMillis)}"
        "UNLOCK_LIMIT" -> "${goal.maxUnlocks} desbloqueos"
        else -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    typeLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${goal.currentStreak} días 🔥",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.extendedColors.brandOrange
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                limitText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
