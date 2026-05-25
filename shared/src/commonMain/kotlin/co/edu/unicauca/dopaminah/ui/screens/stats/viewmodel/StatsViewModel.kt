package co.edu.unicauca.dopaminah.ui.screens.stats.viewmodel

import co.edu.unicauca.dopaminah.domain.repository.DailyDetailStats
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StatsTab { WEEKLY, MONTHLY }

data class AppUsageEntry(
    val appName: String,
    val averageHours: Float
)

data class StatsState(
    val selectedTab: StatsTab = StatsTab.WEEKLY,
    val dailyAverageText: String = "-",
    val unlockAverageText: String = "-",
    val lastWeekUsage: List<Float> = emptyList(),
    val appUsageData: List<AppUsageEntry> = emptyList(),
    val hourlyUsage: List<Float> = emptyList(),
    val selectedDayOffset: Int = 0,
    val dailyDetails: DailyDetailStats? = null,
    val isLoading: Boolean = false
)

class StatsViewModel(
    private val repository: DeviceUsageRepository? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(StatsState(
        dailyAverageText = "3h 12m",
        unlockAverageText = "45/día",
        lastWeekUsage = listOf(2.5f, 3.1f, 4.0f, 2.8f, 3.5f, 4.2f, 3.0f),
        appUsageData = listOf(
            AppUsageEntry("Instagram", 1.5f),
            AppUsageEntry("WhatsApp", 1.2f),
            AppUsageEntry("YouTube", 0.8f),
            AppUsageEntry("TikTok", 0.6f),
        ),
        hourlyUsage = List(24) { i -> if (i in 8..23) (kotlin.math.sin(i.toFloat() / 4f) + 1f) * 15f else 0f },
        dailyDetails = DailyDetailStats("lunes, 24 de marzo", "7:45 AM", 12, "Instagram", "1h 32m", 15, 3600000L)
    ))
    val uiState: StateFlow<StatsState> = _uiState.asStateFlow()

    fun selectTab(tab: StatsTab) {
        if (_uiState.value.selectedTab != tab) {
            _uiState.value = _uiState.value.copy(selectedTab = tab)
        }
    }

    fun goToPreviousDay() {
        val current = _uiState.value.selectedDayOffset
        if (current < 30) {
            _uiState.value = _uiState.value.copy(selectedDayOffset = current + 1)
        }
    }

    fun goToNextDay() {
        val current = _uiState.value.selectedDayOffset
        if (current > 0) {
            _uiState.value = _uiState.value.copy(selectedDayOffset = current - 1)
        }
    }

    fun selectDay(dayOffset: Int) {
        val clamped = dayOffset.coerceIn(0, 30)
        if (_uiState.value.selectedDayOffset != clamped) {
            _uiState.value = _uiState.value.copy(selectedDayOffset = clamped)
        }
    }
}
