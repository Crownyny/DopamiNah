package co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel

import co.edu.unicauca.dopaminah.domain.model.AppLimitGoal
import co.edu.unicauca.dopaminah.domain.model.UserGamificationStats
import co.edu.unicauca.dopaminah.domain.repository.DeviceUsageRepository
import co.edu.unicauca.dopaminah.domain.repository.GamificationRepository
import co.edu.unicauca.dopaminah.domain.repository.GoalsRepository
import co.edu.unicauca.dopaminah.domain.utils.Badge
import co.edu.unicauca.dopaminah.domain.utils.BadgeDefinitions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BadgeUi(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val unlockDate: String?,
    val isUnlocked: Boolean = unlockDate != null
)

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

class AchievementsViewModel(
    gamificationRepository: GamificationRepository? = null,
    private val deviceUsageRepository: DeviceUsageRepository? = null,
    private val goalsRepository: GoalsRepository? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(AchievementsState())
    val state: StateFlow<AchievementsState> = _state.asStateFlow()

    private var last7DaysUsage: List<Long> = emptyList()
    private var userGoals: List<AppLimitGoal> = emptyList()

    init {
        if (gamificationRepository != null) {
            scope.launch {
                gamificationRepository.getGamificationStats().collect { stats ->
                    recompute(stats)
                }
            }
            scope.launch {
                if (deviceUsageRepository != null) {
                    last7DaysUsage = deviceUsageRepository.getDailyUsageForLastDays(7)
                }
                recomputeCurrent()
            }
            scope.launch {
                if (goalsRepository != null) {
                    goalsRepository.getAllGoals().collect { goals ->
                        userGoals = goals
                        recomputeCurrent()
                    }
                }
            }
        } else {
            _state.value = computeState(UserGamificationStats(), emptyList(), emptyList())
        }
    }

    private var lastStats: UserGamificationStats? = null

    private suspend fun recompute(stats: UserGamificationStats) {
        lastStats = stats
        if (deviceUsageRepository != null) {
            last7DaysUsage = deviceUsageRepository.getDailyUsageForLastDays(7)
        }
        _state.value = computeState(stats, last7DaysUsage, userGoals)
    }

    private fun recomputeCurrent() {
        val stats = lastStats ?: return
        _state.value = computeState(stats, last7DaysUsage, userGoals)
    }

    private fun computeState(
        stats: UserGamificationStats,
        dailyUsage: List<Long>,
        goals: List<AppLimitGoal>
    ): AchievementsState {
        val badges = BadgeDefinitions.allBadges.map { badge ->
            val isUnlocked = isBadgeUnlocked(badge, stats, dailyUsage, goals)
            BadgeUi(
                id = badge.id,
                emoji = badge.emoji,
                title = badge.title,
                description = badge.description,
                unlockDate = if (isUnlocked) "Desbloqueada" else null
            )
        }
        val unlockedCount = badges.count { it.isUnlocked }
        val nextBadgeObj = BadgeDefinitions.allBadges.firstOrNull {
            !isBadgeUnlocked(it, stats, dailyUsage, goals)
        }

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

    private fun isBadgeUnlocked(
        badge: Badge,
        stats: UserGamificationStats,
        dailyUsage: List<Long>,
        goals: List<AppLimitGoal>
    ): Boolean {
        return when (badge.requirement) {
            "streak_3" -> stats.bestStreak >= 3
            "streak_7" -> stats.bestStreak >= 7
            "streak_14" -> stats.bestStreak >= 14
            "streak_30" -> stats.bestStreak >= 30
            "daily_goal_1" -> stats.totalPoints >= 10
            "focus_hours_10" -> stats.totalPoints >= 100
            "reduction_50" -> hasReducedBy50Percent(dailyUsage)
            "under_1h_3days" -> hasBeenUnder1HourFor3Days(dailyUsage)
            else -> false
        }
    }

    private fun hasReducedBy50Percent(dailyUsage: List<Long>): Boolean {
        if (dailyUsage.size < 7) return false
        val recent3 = dailyUsage.takeLast(3).average().toLong()
        val previous4 = dailyUsage.take(4).average().toLong()
        if (previous4 <= 0) return false
        return recent3 <= previous4 / 2
    }

    private fun hasBeenUnder1HourFor3Days(dailyUsage: List<Long>): Boolean {
        if (dailyUsage.size < 3) return false
        return dailyUsage.takeLast(3).all { it <= 3_600_000L }
    }
}
