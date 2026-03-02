package co.edu.unicauca.dopaminah.ui.screens.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val deviceUsageRepository: DeviceUsageRepository
) : ViewModel() {

    private val _gamificationState = MutableStateFlow(UserGamificationStats())
    val gamificationState: StateFlow<UserGamificationStats> = _gamificationState.asStateFlow()

    private val _dailyUnlocks = MutableStateFlow(0)
    val dailyUnlocks: StateFlow<Int> = _dailyUnlocks.asStateFlow()

    private val _yesterdayUnlocks = MutableStateFlow(0)
    val yesterdayUnlocks: StateFlow<Int> = _yesterdayUnlocks.asStateFlow()

    private val _hasUsagePermission = MutableStateFlow(false)
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission.asStateFlow()

    init {
        loadGamificationStats()
        checkAndIncrementStreak()
        loadUnlockStats()
    }

    private fun loadGamificationStats() {
        viewModelScope.launch {
            gamificationRepository.getGamificationStats()
                .catch { e ->
                    // Handle error if needed
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
                _dailyUnlocks.value = today
                _yesterdayUnlocks.value = yesterday
            } else {
                _dailyUnlocks.value = 0
                _yesterdayUnlocks.value = 0
            }
        }
    }

    fun checkAndIncrementStreak() {
        viewModelScope.launch {
            gamificationRepository.incrementStreakAndPoints()
        }
    }
    
    fun refreshStats() {
        loadUnlockStats()
    }
}
