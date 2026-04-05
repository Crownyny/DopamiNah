package co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicauca.dopaminah.domain.model.AppUsageSummary
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.usecase.GetDashboardDataUseCase
import co.edu.unicauca.dopaminah.domain.usecase.UpdateStreakUseCase
import co.edu.unicauca.dopaminah.domain.usecase.AppLimitCardInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val deviceUsageRepository: DeviceUsageRepository,
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val updateStreakUseCase: UpdateStreakUseCase,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _gamificationState = MutableStateFlow(UserGamificationStats())
    val gamificationState: StateFlow<UserGamificationStats> = _gamificationState.asStateFlow()

    private val _dailyUnlocks = MutableStateFlow(0)
    val dailyUnlocks: StateFlow<Int> = _dailyUnlocks.asStateFlow()

    private val _yesterdayUnlocks = MutableStateFlow(0)
    val yesterdayUnlocks: StateFlow<Int> = _yesterdayUnlocks.asStateFlow()

    private val _totalDailyUsageMs = MutableStateFlow(0L)
    val totalDailyUsageMs: StateFlow<Long> = _totalDailyUsageMs.asStateFlow()

    private val _dailyUsageStats = MutableStateFlow<List<AppUsageSummary>>(emptyList())
    val dailyUsageStats: StateFlow<List<AppUsageSummary>> = _dailyUsageStats.asStateFlow()

    private val _hasUsagePermission = MutableStateFlow(false)
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission.asStateFlow()

    private val _appLimitCards = MutableStateFlow<List<AppLimitCardInfo>>(emptyList())
    val appLimitCards: StateFlow<List<AppLimitCardInfo>> = _appLimitCards.asStateFlow()

    init {
        loadGamificationStats()
        checkAndIncrementStreak()
        loadUnlockStats()
        observeAppLimits()
    }

    private fun observeAppLimits() {
        viewModelScope.launch {
            getDashboardDataUseCase.getAppLimitCards(_dailyUsageStats)
                .collect { cards ->
                    _appLimitCards.value = cards
                }
        }
    }

    private fun loadGamificationStats() {
        viewModelScope.launch {
            gamificationRepository.getGamificationStats()
                .catch {
                }
                .collect { stats ->
                    _gamificationState.value = stats
                }
        }
    }

    private fun loadUnlockStats() {
        viewModelScope.launch {
            val hasPerm = deviceUsageRepository.hasUsageStatsPermission()
            _hasUsagePermission.value = hasPerm

            if (hasPerm) {
                val today = deviceUsageRepository.getDailyDeviceUnlocks()
                val yesterday = deviceUsageRepository.getYesterdayDeviceUnlocks()
                val usageStats = deviceUsageRepository.getDailyUsageStats()

                _dailyUnlocks.value = today
                _yesterdayUnlocks.value = yesterday

                val ownPackage = appContext.packageName
                val allApps = usageStats.filter { summary ->
                    summary.packageName != ownPackage
                }

                _dailyUsageStats.value = allApps
                _totalDailyUsageMs.value = usageStats.sumOf { it.totalTimeForegroundMillis }
            } else {
                _dailyUnlocks.value = 0
                _yesterdayUnlocks.value = 0
                _dailyUsageStats.value = emptyList()
                _totalDailyUsageMs.value = 0L
            }
        }
    }

    fun checkAndIncrementStreak() {
        viewModelScope.launch {
            updateStreakUseCase.execute()
        }
    }

    fun refreshStats() {
        loadUnlockStats()
    }
}
