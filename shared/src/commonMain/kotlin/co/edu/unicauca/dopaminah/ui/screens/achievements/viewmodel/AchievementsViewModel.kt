package co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel

import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.utils.Badge
import co.edu.unicauca.dopaminah.domain.utils.BadgeDefinitions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Display model for a badge in the achievements grid. */
data class BadgeUi(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val unlockDate: String?,
    val isUnlocked: Boolean = unlockDate != null
)

/** UI state for the achievements screen. */
data class AchievementsState(
    val streakDays: Int = 0,
    val level: Int = 1,
    val badges: List<BadgeUi> = emptyList(),
    val nextBadgeId: String? = null,
    val nextBadgeEmoji: String = "🎯",
    val nextBadgeTitle: String = "Primer Paso",
    val nextBadgeDescription: String = "Completa tu primera meta diaria",
    val unlockedCount: Int = 0,
    val totalCount: Int = 0,
    val bestStreak: Int = 0
)

/** ViewModel for the achievements screen, computing badge unlock status, streak, and level from gamification stats. */
class AchievementsViewModel(
    gamificationRepository: GamificationRepository? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(AchievementsState())
    val state: StateFlow<AchievementsState> = _state.asStateFlow()

    init {
        if (gamificationRepository != null) {
            scope.launch {
                gamificationRepository.getGamificationStats().collect { stats ->
                    _state.value = computeState(stats)
                }
            }
        } else {
            _state.value = computeState(UserGamificationStats())
        }
    }

    private fun computeState(stats: UserGamificationStats): AchievementsState {
        val badges = BadgeDefinitions.allBadges.map { badge ->
            val isUnlocked = isBadgeUnlocked(badge, stats)
            BadgeUi(
                id = badge.id,
                emoji = badge.emoji,
                title = badge.title,
                description = badge.description,
                unlockDate = if (isUnlocked) "Desbloqueada" else null
            )
        }
        val unlockedCount = badges.count { it.isUnlocked }
        val nextBadgeObj = BadgeDefinitions.allBadges.firstOrNull { !isBadgeUnlocked(it, stats) }

        return AchievementsState(
            streakDays = stats.streak,
            level = stats.level,
            badges = badges,
            nextBadgeId = nextBadgeObj?.id,
            nextBadgeEmoji = nextBadgeObj?.emoji ?: "🎉",
            nextBadgeTitle = nextBadgeObj?.title ?: "Todas completadas",
            nextBadgeDescription = nextBadgeObj?.description ?: "",
            unlockedCount = unlockedCount,
            totalCount = badges.size,
            bestStreak = stats.bestStreak
        )
    }

    private fun isBadgeUnlocked(badge: Badge, stats: UserGamificationStats): Boolean {
        return when (badge.requirement) {
            "streak_3" -> stats.bestStreak >= 3
            "streak_7" -> stats.bestStreak >= 7
            "streak_14" -> stats.bestStreak >= 14
            "streak_30" -> stats.bestStreak >= 30
            "daily_goal_1" -> stats.totalPoints >= 10
            "focus_hours_10" -> stats.totalPoints >= 100
            "reduction_50" -> stats.totalPoints >= 50
            "under_1h_3days" -> stats.bestStreak >= 3
            else -> false
        }
    }
}
