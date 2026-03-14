package co.edu.unicauca.dopaminah.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.ui.icons.LucideLock
import co.edu.unicauca.dopaminah.ui.icons.LucideSmartphone
import co.edu.unicauca.dopaminah.ui.icons.LucideTimer
import co.edu.unicauca.dopaminah.ui.screens.goals.components.AddGoalButton
import co.edu.unicauca.dopaminah.ui.screens.goals.components.CreateGoalDialog
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalCard
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalsHeader
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalsTipCard
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalsViewModel
import co.edu.unicauca.dopaminah.ui.theme.extendedColors

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        GoalsHeader()

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Render list of goals from ViewModel state
            if (state.goals.isEmpty()) {
                // Temporary Hardcoded mockup while waiting for Room DB connection
                GoalCard(
                    title = "Tiempo Total Diario",
                    subtitle = "Máximo: 2h 0m",
                    icon = LucideTimer,
                    iconBgColor = MaterialTheme.extendedColors.dangerRed.copy(alpha = 0.1f),
                    iconTintColor = MaterialTheme.extendedColors.dangerRed, 
                    progressLabel = "Progreso Hoy",
                    progressPercent = "292%",
                    progressFraction = 1f,
                    isExceeded = true
                )

                GoalCard(
                    title = "Límite Instagram",
                    subtitle = "Máximo: 20m",
                    icon = LucideSmartphone,
                    iconBgColor = MaterialTheme.extendedColors.dangerRed.copy(alpha = 0.1f), 
                    iconTintColor = MaterialTheme.extendedColors.dangerRed, 
                    progressLabel = "Progreso Hoy",
                    progressPercent = "280%",
                    progressFraction = 1f, 
                    isExceeded = true
                )

                GoalCard(
                    title = "Límite de Desbloqueos",
                    subtitle = "Máximo: 50 desbloqueos",
                    icon = LucideLock,
                    iconBgColor = MaterialTheme.extendedColors.dangerRed.copy(alpha = 0.1f),
                    iconTintColor = MaterialTheme.extendedColors.dangerRed,
                    progressLabel = "Progreso Hoy",
                    progressPercent = "178%",
                    progressFraction = 1f, 
                    isExceeded = true
                )
            } else {
                state.goals.forEach { goal ->
                    GoalCard(
                        title = goal.title,
                        subtitle = goal.subtitle,
                        icon = goal.icon,
                        iconBgColor = goal.iconBgColor,
                        iconTintColor = goal.iconTintColor,
                        progressLabel = goal.progressLabel,
                        progressPercent = goal.progressPercent,
                        progressFraction = goal.progressFraction,
                        isExceeded = goal.isExceeded
                    )
                }
            }

            // Add Goal Button
            AddGoalButton(onClick = { viewModel.showCreateGoalDialog() })

            // Tip Card
            GoalsTipCard()

            Spacer(modifier = Modifier.height(32.dp)) 
        }
    }

    if (state.showCreateDialog) {
        CreateGoalDialog(
            installedApps = state.installedApps,
            onDismiss = { viewModel.hideCreateGoalDialog() },
            onSave = { type, appName, limit ->
                viewModel.submitNewGoal(type, appName, limit)
            }
        )
    }
}
