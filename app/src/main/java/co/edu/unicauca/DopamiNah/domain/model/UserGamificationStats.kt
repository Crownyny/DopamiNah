package co.edu.unicauca.DopamiNah.domain.model

data class UserGamificationStats(
    val level: Int = 1,
    val currentPoints: Int = 0,
    val pointsToNextLevel: Int = 100,
    val activeBadges: List<String> = emptyList()
)
