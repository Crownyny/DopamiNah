package co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel

import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.utils.BadgeDefinitions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AchievementsViewModel(
    gamificationRepository: GamificationRepository? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val badges = BadgeDefinitions.allBadges

    private val _stats = MutableStateFlow(UserGamificationStats())
    val stats: StateFlow<UserGamificationStats> = _stats.asStateFlow()

    val currentLevel: Int get() = _stats.value.level
    val currentStreak: Int get() = _stats.value.streak

    init {
        if (gamificationRepository != null) {
            scope.launch {
                gamificationRepository.getGamificationStats().collect { s ->
                    _stats.value = s
                }
            }
        }
    }
}
