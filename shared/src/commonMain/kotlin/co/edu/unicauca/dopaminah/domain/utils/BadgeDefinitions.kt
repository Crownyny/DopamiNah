package co.edu.unicauca.dopaminah.domain.utils

/** A single achievement badge with its unlock requirement key. */
data class Badge(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
    val requirement: String
)

/** Catalog of all badges and utility functions for level titles and streak motivation messages. */
object BadgeDefinitions {

    val allBadges = listOf(
        Badge("primer_paso", "👶", "Primer Paso", "Completa tu primera meta diaria", "daily_goal_1"),
        Badge("racha_3", "🌱", "Racha Verde", "Mantén una racha de 3 días", "streak_3"),
        Badge("racha_7", "🔥", "Racha de Fuego", "Mantén una racha de 7 días", "streak_7"),
        Badge("racha_14", "💪", "Fuerza de Voluntad", "Mantén una racha de 14 días", "streak_14"),
        Badge("racha_30", "🏆", "Leyenda", "Mantén una racha de 30 días", "streak_30"),
        Badge("focus", "🎯", "Maestro del Enfoque", "Acumula 10 horas de enfoque", "focus_hours_10"),
        Badge("reduction", "📉", "Reductor", "Reduce tu tiempo en pantalla un 50%", "reduction_50"),
        Badge("autodisciplina", "🧘", "Autodisciplina", "Usa menos de 1 hora diaria por 3 días", "under_1h_3days"),
    )

    fun getLevelTitle(level: Int): String = when (level) {
        1 -> "Aprendiz Digital"
        2 -> "Explorador Consciente"
        3 -> "Guerrero del Enfoque"
        4 -> "Sabio del Tiempo"
        5 -> "Maestro Zen"
        else -> "Leyenda de la Dopamina"
    }

    fun getStreakMotivation(streak: Int): String = when {
        streak >= 30 -> "Increíble! $streak días sin parar 🔥"
        streak >= 14 -> "Estás en racha! Sigue así 💪"
        streak >= 7 -> "Una semana completa! 🎉"
        streak >= 3 -> "Buen comienzo! No pares 🚀"
        else -> "Cada día cuenta. Tú puedes ✨"
    }
}
