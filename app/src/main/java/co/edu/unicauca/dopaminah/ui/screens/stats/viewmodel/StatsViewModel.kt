package co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicauca.dopaminah.domain.repository.DailyDetailStats
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class StatsTab {
    WEEKLY, MONTHLY
}

data class AppUsageEntry(
    val appName: String,
    val averageHours: Float // average hours per day
)

data class StatsState(
    val selectedTab: StatsTab = StatsTab.WEEKLY,
    val dailyAverageText: String = "-",
    val unlockAverageText: String = "-",
    val lastWeekUsage: List<Float> = emptyList(),
    val appUsageData: List<AppUsageEntry> = emptyList(),
    val selectedDayOffset: Int = 0,       // 0 = today, 1 = yesterday...
    val dailyDetails: DailyDetailStats? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: DeviceUsageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StatsState(isLoading = true))
    val uiState: StateFlow<StatsState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val days = if (_uiState.value.selectedTab == StatsTab.WEEKLY) 7 else 30
            val avgUsage = repository.getAverageUsageMillis(days)
            val avgUnlocks = repository.getAverageUnlocks(days)
            
            val historyMillis = repository.getDailyUsageForLastDays(7)
            val historyHours = historyMillis.map { it.toFloat() / (1000f * 60f * 60f) }

            val rawAppData = repository.getAverageUsagePerApp(days)
            val appEntries = rawAppData.map { (name, millis) ->
                AppUsageEntry(
                    appName = name,
                    averageHours = millis.toFloat() / (1000f * 60f * 60f)
                )
            }

            val details = repository.getDailyDetails(_uiState.value.selectedDayOffset)
            
            _uiState.value = _uiState.value.copy(
                dailyAverageText = formatTime(avgUsage),
                unlockAverageText = "$avgUnlocks/día",
                lastWeekUsage = historyHours,
                appUsageData = appEntries,
                dailyDetails = details,
                isLoading = false
            )
        }
    }

    fun selectTab(tab: StatsTab) {
        if (_uiState.value.selectedTab != tab) {
            _uiState.value = _uiState.value.copy(selectedTab = tab)
            loadData()
        }
    }

    fun goToPreviousDay() {
        // max 30 days back
        val current = _uiState.value.selectedDayOffset
        if (current < 30) {
            _uiState.value = _uiState.value.copy(selectedDayOffset = current + 1)
            reloadDailyDetails()
        }
    }

    fun goToNextDay() {
        val current = _uiState.value.selectedDayOffset
        if (current > 0) {
            _uiState.value = _uiState.value.copy(selectedDayOffset = current - 1)
            reloadDailyDetails()
        }
    }

    fun selectDay(dayOffset: Int) {
        val clamped = dayOffset.coerceIn(0, 30)
        if (_uiState.value.selectedDayOffset != clamped) {
            _uiState.value = _uiState.value.copy(selectedDayOffset = clamped)
            reloadDailyDetails()
        }
    }

    private fun reloadDailyDetails() {
        viewModelScope.launch {
            val details = repository.getDailyDetails(_uiState.value.selectedDayOffset)
            _uiState.value = _uiState.value.copy(dailyDetails = details)
        }
    }

    private fun formatTime(millis: Long): String {
        val totalMinutes = millis / 1000 / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }
}
