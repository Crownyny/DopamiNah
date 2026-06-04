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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.dopaminah.ui.screens.goals.components.AddGoalButton
import co.edu.unicauca.dopaminah.ui.screens.goals.components.CreateGoalDialog
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalCard
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalsHeader
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalsTipCard
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalsViewModel

@Composable
fun GoalsScreen(viewModel: GoalsViewModel? = null) {
    val vm = viewModel ?: return
    val state by vm.state.collectAsState()

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
                        modifier = Modifier.fillMaxWidth().height(120.dp),
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
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes metas definidas aun.\nToca \"Agregar Meta\" para comenzar.",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    state.goals.forEach { goal ->
                        GoalCard(
                            goal = goal,
                            onDelete = { vm.deleteGoal(goal.id) },
                            onEdit = { newLimit -> vm.editGoal(goal.id, newLimit) }
                        )
                    }
                }
            }

            AddGoalButton(onClick = { vm.showCreateGoalDialog() })

            GoalsTipCard()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (state.showCreateDialog) {
        CreateGoalDialog(
            installedApps = state.installedApps,
            onDismiss = { vm.hideCreateGoalDialog() },
            onSave = { type, appName, limit ->
                vm.submitNewGoal(type, appName, limit)
            }
        )
    }
}
