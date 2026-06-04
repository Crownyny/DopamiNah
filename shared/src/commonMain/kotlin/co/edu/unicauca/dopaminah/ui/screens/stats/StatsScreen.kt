package co.edu.unicauca.dopaminah.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.ui.screens.stats.components.StatsHeader
import co.edu.unicauca.dopaminah.ui.screens.stats.components.StatsSummaryCard
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsViewModel

@Composable
fun StatsScreen(viewModel: StatsViewModel? = null) {
    val vm = viewModel ?: remember { StatsViewModel() }
    val uiState by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        StatsHeader()
        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            StatsSummaryCard(
                label = "Promedio Diario",
                value = uiState.dailyAverageText
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
