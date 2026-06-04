package co.edu.unicauca.dopaminah.ui.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalCard
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalsHeader
import co.edu.unicauca.dopaminah.ui.screens.goals.components.GoalsTipCard
import co.edu.unicauca.dopaminah.ui.screens.goals.viewmodel.GoalsViewModel

@Composable
fun GoalsScreen(viewModel: GoalsViewModel? = null) {
    val vm = viewModel ?: remember { GoalsViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            "Metas y Límites",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Reduce tu tiempo en pantalla",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))
        GoalsHeader()

        Spacer(modifier = Modifier.height(16.dp))
        GoalsTipCard()

        Spacer(modifier = Modifier.height(24.dp))
        vm.goals.forEach { goal ->
            GoalCard(goal = goal)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
