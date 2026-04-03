package co.edu.unicauca.dopaminah.ui.screens.achievements.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ── UI-only data models ──────────────────────────────────────────────────

data class BadgeUi(
    val emoji: String,
    val title: String,
    val description: String,
    val unlockDate: String?,
    val isUnlocked: Boolean = unlockDate != null
)

data class AchievementsState(
    val streakDays: Int = 7,
    val level: Int = 5,
    val badges: List<BadgeUi> = listOf(
        BadgeUi(
            emoji       = "🎯",
            title       = "Primer Paso",
            description = "Completa tu primera meta diaria",
            unlockDate  = "22/3/2026"
        ),
        BadgeUi(
            emoji       = "🔥",
            title       = "Racha de Fuego",
            description = "Mantén una racha de 7 días",
            unlockDate  = "22/3/2026"
        ),
        BadgeUi(emoji = "", title = "", description = "", unlockDate = null),
        BadgeUi(emoji = "", title = "", description = "", unlockDate = null),
        BadgeUi(emoji = "", title = "", description = "", unlockDate = null),
        BadgeUi(emoji = "", title = "", description = "", unlockDate = null),
    ),
    val nextBadgeEmoji: String       = "💪",
    val nextBadgeTitle: String       = "Autodisciplina",
    val nextBadgeDescription: String = "Usa menos de 1 hora diaria por 3 días consecutivos",
    val unlockedCount: Int           = 2,
    val totalCount: Int              = 6,
    val bestStreak: Int              = 7
)

class AchievementsViewModel : ViewModel() {
    private val _state = MutableStateFlow(AchievementsState())
    val state: StateFlow<AchievementsState> = _state.asStateFlow()
}
