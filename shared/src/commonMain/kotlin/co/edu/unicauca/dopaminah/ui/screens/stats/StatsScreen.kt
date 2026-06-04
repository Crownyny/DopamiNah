package co.edu.unicauca.dopaminah.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.unicauca.dopaminah.ui.screens.stats.components.DailyDetailsCard
import co.edu.unicauca.dopaminah.ui.screens.stats.components.DatePickerSheet
import co.edu.unicauca.dopaminah.ui.screens.stats.components.StatsCarousel
import co.edu.unicauca.dopaminah.ui.screens.stats.components.StatsHeader
import co.edu.unicauca.dopaminah.ui.screens.stats.components.StatsSummaryCards
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsViewModel

@Composable
fun StatsScreen(viewModel: StatsViewModel? = null) {
    val vm = viewModel ?: remember { StatsViewModel() }
    val uiState by vm.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        StatsHeader(
            selectedTab = uiState.selectedTab,
            onTabSelected = { vm.selectTab(it) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        StatsSummaryCards(
            dailyAverageText = uiState.dailyAverageText,
            unlockAverageText = uiState.unlockAverageText
        )
        Spacer(modifier = Modifier.height(24.dp))
        StatsCarousel(state = uiState)
        Spacer(modifier = Modifier.height(32.dp))
        DailyDetailsCard(
            details = uiState.dailyDetails,
            selectedDayOffset = uiState.selectedDayOffset,
            onPreviousDay = { vm.goToPreviousDay() },
            onNextDay = { vm.goToNextDay() },
            onSelectDay = { showDatePicker = true }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showDatePicker) {
        DatePickerSheet(
            selectedDayOffset = uiState.selectedDayOffset,
            onSelectDay = { offset ->
                vm.selectDay(offset)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
