package co.edu.unicauca.DopamiNah.ui.screens.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unicauca.DopamiNah.domain.model.UserGamificationStats
import co.edu.unicauca.DopamiNah.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _gamificationState = MutableStateFlow(UserGamificationStats())
    val gamificationState: StateFlow<UserGamificationStats> = _gamificationState.asStateFlow()

    init {
        loadGamificationStats()
        incrementStreak()
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

    private fun incrementStreak() {
        viewModelScope.launch {
            gamificationRepository.incrementStreakAndPoints()
        }
    }
}
