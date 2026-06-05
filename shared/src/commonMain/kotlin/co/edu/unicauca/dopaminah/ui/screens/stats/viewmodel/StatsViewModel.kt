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

/** Time range tab for the stats view. */
enum class StatsTab { WEEKLY, MONTHLY }

/** Usage summary for a single app, used in stats charts. */
data class AppUsageEntry(
    val appName: String,
    val averageHours: Float
)

/** UI state for the stats screen, holding all chart and detail data. */
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

/** ViewModel for the usage stats screen, providing weekly/monthly data, app breakdown, and hourly charts. */
class StatsViewModel(
    private val repository: DeviceUsageRepository? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(StatsState(isLoading = true))
    val uiState: StateFlow<StatsState> = _uiState.asStateFlow()

    init {
        if (repository != null) loadData()
        else _uiState.value = StatsState(
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
        )
    }

    private fun loadData() {
        val repo = repository ?: return
        scope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val days = if (_uiState.value.selectedTab == StatsTab.WEEKLY) 7 else 30
            val avgUsage = repo.getAverageUsageMillis(days)
            val avgUnlocks = repo.getAverageUnlocks(days)
            val historyMillis = repo.getDailyUsageForLastDays(7)
            val historyHours = historyMillis.map { it.toFloat() / (1000f * 60f * 60f) }
            val hourlyUsage = repo.getHourlyUsage(days)
            val rawAppData = repo.getAverageUsagePerApp(days)
            val appEntries = rawAppData.map { (name, millis) ->
                AppUsageEntry(
                    appName = name,
                    averageHours = millis.toFloat() / (1000f * 60f * 60f)
                )
            }
            val details = repo.getDailyDetails(_uiState.value.selectedDayOffset)
            _uiState.value = _uiState.value.copy(
                dailyAverageText = formatTime(avgUsage),
                unlockAverageText = "$avgUnlocks/día",
                lastWeekUsage = historyHours,
                appUsageData = appEntries,
                hourlyUsage = hourlyUsage,
                dailyDetails = details,
                isLoading = false
            )
        }
    }

    fun selectTab(tab: StatsTab) {
        if (_uiState.value.selectedTab != tab) {
            _uiState.value = _uiState.value.copy(selectedTab = tab)
            if (repository != null) loadData()
        }
    }

    fun goToPreviousDay() {
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
        scope.launch {
            if (repository != null) {
                val details = repository.getDailyDetails(_uiState.value.selectedDayOffset)
                _uiState.value = _uiState.value.copy(dailyDetails = details)
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val totalMinutes = millis / 1000 / 60
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }
}
