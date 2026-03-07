package co.edu.unicauca.dopaminah.ui.screens.stats

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import co.edu.unicauca.dopaminah.ui.screens.stats.components.DailyDetailsCard
import co.edu.unicauca.dopaminah.ui.screens.stats.components.DatePickerSheet
import co.edu.unicauca.dopaminah.ui.screens.stats.components.StatsCarousel
import co.edu.unicauca.dopaminah.ui.screens.stats.components.StatsHeader
import co.edu.unicauca.dopaminah.ui.screens.stats.components.StatsSummaryCards
import co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel.StatsViewModel

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    if (showDatePicker) {
        DatePickerSheet(
            selectedDayOffset = uiState.selectedDayOffset,
            onSelectDay = { offset ->
                viewModel.selectDay(offset)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // We don't use paddingValues top here because we want the header to go under the status bar
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            StatsHeader(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )

            StatsSummaryCards(
                dailyAverageText = uiState.dailyAverageText,
                unlockAverageText = uiState.unlockAverageText
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatsCarousel(state = uiState)

            Spacer(modifier = Modifier.height(24.dp))
            
            // Placed outside the carousel, effectively below it
            DailyDetailsCard(
                details = uiState.dailyDetails,
                selectedDayOffset = uiState.selectedDayOffset,
                onPreviousDay = { viewModel.goToPreviousDay() },
                onNextDay = { viewModel.goToNextDay() },
                onSelectDay = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
