package co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class StatsTab {
    WEEKLY, MONTHLY
}

data class StatsState(
    val selectedTab: StatsTab = StatsTab.WEEKLY,
    val dailyAverageText: String = "4h 55m",
    val unlockAverageText: String = "66/día",
    // These will eventually hold real data from the repository
    val isLoading: Boolean = false
)

class StatsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StatsState())
    val uiState: StateFlow<StatsState> = _uiState.asStateFlow()

    fun selectTab(tab: StatsTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        // TODO: Load data corresponding to the selected tab later
    }
}
