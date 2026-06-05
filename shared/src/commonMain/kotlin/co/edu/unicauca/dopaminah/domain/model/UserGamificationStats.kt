package co.edu.unicauca.dopaminah.domain.model

/** Gamification state for a user: level, points (current/total), streak metrics, and active badges. */
data class UserGamificationStats(
    val level: Int = 1,
    val currentPoints: Int = 0,
    val pointsToNextLevel: Int = 100,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val totalPoints: Int = 0,
    val activeBadges: List<String> = emptyList()
)
