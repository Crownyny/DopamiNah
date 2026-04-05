package co.edu.unicauca.dopaminah.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.edu.unicauca.dopaminah.R
import co.edu.unicauca.dopaminah.ui.icons.LucideLock
import co.edu.unicauca.dopaminah.ui.icons.LucideSmartphone
import co.edu.unicauca.dopaminah.ui.icons.LucideTimer
import co.edu.unicauca.dopaminah.ui.screens.goals.components.AddGoalButton
import co.edu.unicauca.dopaminah.ui.screens.goals.components.CreateGoalDialog
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalCard
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalsHeader
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalsTipCard
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalType
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
        GoalsHeader()

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                state.goals.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.goals_empty_state),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    state.goals.forEach { goal ->
                        val icon = when (goal.goalType) {
                            GoalType.TOTAL_DAILY -> LucideTimer
                            GoalType.APP_LIMIT -> LucideSmartphone
                            GoalType.UNLOCK_LIMIT -> LucideLock
                            else -> LucideTimer
                        }
                        val iconBgColor = if (goal.isExceeded)
                            MaterialTheme.extendedColors.dangerRed.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

                        val iconTintColor = if (goal.isExceeded)
                            MaterialTheme.extendedColors.dangerRed
                        else
                            MaterialTheme.colorScheme.primary

                        GoalCard(
                            title = goal.title,
                            subtitle = goal.subtitle,
                            icon = icon,
                            iconBgColor = iconBgColor,
                            iconTintColor = iconTintColor,
                            progressLabel = goal.progressLabel,
                            progressPercent = goal.progressPercent,
                            progressFraction = goal.progressFraction,
                            isExceeded = goal.isExceeded,
                            appPackageName = goal.appPackageName,
                            goalType = goal.goalType,
                            currentLimitMinutes = goal.currentLimitMinutes,
                            onDelete = { viewModel.deleteGoal(goal.id) },
                            onEdit = { newLimit -> viewModel.editGoal(goal.id, newLimit) }
                        )
                    }
                }
            }

            AddGoalButton(onClick = { viewModel.showCreateGoalDialog() })

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
