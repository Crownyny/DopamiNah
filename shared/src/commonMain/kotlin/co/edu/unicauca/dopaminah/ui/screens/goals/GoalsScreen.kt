package co.edu.unicauca.dopaminah.ui.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import co.edu.unicauca.dopaminah.ui.theme.DopaminahOrange
import co.edu.unicauca.dopaminah.ui.theme.extendedColors
import co.edu.unicauca.dopaminah.utils.UsageTimeUtils

@Composable
fun GoalsScreen() {
    val goals = remember {
        listOf(
            AppLimitGoal(id = 1, goalType = "TOTAL_DAILY", maxTimeMillis = 14400000L, currentStreak = 3),
            AppLimitGoal(id = 2, goalType = "APP_LIMIT", packageName = "com.instagram.android", appDisplayName = "Instagram", maxTimeMillis = 3600000L, currentStreak = 5),
            AppLimitGoal(id = 3, goalType = "UNLOCK_LIMIT", maxUnlocks = 50, currentStreak = 2),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Metas y Límites", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Reduce tu tiempo en pantalla", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DopaminahOrange),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("¡Alcanza tu libertad digital!", color = androidx.compose.ui.graphics.Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Define límites inteligentes y recupera tu tiempo para lo que realmente importa.", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.extendedColors.aboutSurface),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("\uD83D\uDCA1 Consejo del Día", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Empieza con metas pequeñas. Reducir 15 minutos al día puede marcar una gran diferencia a largo plazo.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        goals.forEach { goal ->
            GoalCardItem(goal = goal)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GoalCardItem(goal: AppLimitGoal) {
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
                Text(typeLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("${goal.currentStreak} días \uD83D\uDD25", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.extendedColors.brandOrange)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(limitText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
