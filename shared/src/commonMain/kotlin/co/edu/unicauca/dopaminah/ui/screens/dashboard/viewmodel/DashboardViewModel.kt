package co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel

import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.usecase.GetDashboardDataUseCase
import co.edu.unicauca.dopaminah.domain.usecase.UpdateStreakUseCase
import co.edu.unicauca.dopaminah.domain.usecase.AppLimitCardInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val gamificationRepository: GamificationRepository? = null,
    private val deviceUsageRepository: DeviceUsageRepository? = null,
    private val getDashboardDataUseCase: GetDashboardDataUseCase? = null,
    private val updateStreakUseCase: UpdateStreakUseCase? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _gamificationState = MutableStateFlow(UserGamificationStats())
    val gamificationState: StateFlow<UserGamificationStats> = _gamificationState.asStateFlow()

    private val _dailyUnlocks = MutableStateFlow(0)
    val dailyUnlocks: StateFlow<Int> = _dailyUnlocks.asStateFlow()

    private val _yesterdayUnlocks = MutableStateFlow(0)
    val yesterdayUnlocks: StateFlow<Int> = _yesterdayUnlocks.asStateFlow()

    private val _totalDailyUsageMs = MutableStateFlow(0L)
    val totalDailyUsageMs: StateFlow<Long> = _totalDailyUsageMs.asStateFlow()

    private val _dailyUsageStats = MutableStateFlow<List<AppUsageSummary>>(listOf(
        AppUsageSummary("com.instagram.android", "Instagram", 3600000L, 10, 0L),
        AppUsageSummary("com.whatsapp", "WhatsApp", 1800000L, 25, 0L),
        AppUsageSummary("com.youtube", "YouTube", 1200000L, 5, 0L),
        AppUsageSummary("com.tiktok", "TikTok", 900000L, 8, 0L),
        AppUsageSummary("com.twitter", "Twitter", 600000L, 12, 0L),
    ))
    val dailyUsageStats: StateFlow<List<AppUsageSummary>> = _dailyUsageStats.asStateFlow()

    private val _hasUsagePermission = MutableStateFlow(true)
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission.asStateFlow()

    private val _appLimitCards = MutableStateFlow<List<AppLimitCardInfo>>(listOf(
        AppLimitCardInfo("com.instagram.android", "Instagram", 1800000L, 3600000L),
        AppLimitCardInfo("com.youtube", "YouTube", 1200000L, 2400000L),
    ))
    val appLimitCards: StateFlow<List<AppLimitCardInfo>> = _appLimitCards.asStateFlow()

    init {
        if (gamificationRepository != null) loadGamificationStats()
        if (deviceUsageRepository != null) loadUnlockStats()
        if (getDashboardDataUseCase != null) observeAppLimits()
    }

    private fun observeAppLimits() {
        scope.launch {
            getDashboardDataUseCase!!.getAppLimitCards(_dailyUsageStats).collect { cards ->
                _appLimitCards.value = cards
            }
        }
    }

    private fun loadGamificationStats() {
        scope.launch {
            gamificationRepository!!.getGamificationStats().collect { stats ->
                _gamificationState.value = stats
            }
        }
    }

    private fun loadUnlockStats() {
        scope.launch {
            val hasPerm = deviceUsageRepository!!.hasUsageStatsPermission()
            _hasUsagePermission.value = hasPerm
            if (hasPerm) {
                val today = deviceUsageRepository.getDailyDeviceUnlocks()
                val yesterday = deviceUsageRepository.getYesterdayDeviceUnlocks()
                val usageStats = deviceUsageRepository.getDailyUsageStats()
                _dailyUnlocks.value = today
                _yesterdayUnlocks.value = yesterday
                _dailyUsageStats.value = usageStats
                _totalDailyUsageMs.value = usageStats.sumOf { it.totalTimeForegroundMillis }
            }
        }
    }

    fun checkAndIncrementStreak() {
        scope.launch { updateStreakUseCase?.execute() }
    }

    fun refreshStats() { loadUnlockStats() }

    companion object {
        fun createEmpty(): DashboardViewModel = DashboardViewModel()
    }
}
