package co.edu.unicauca.dopaminah.domain.model

/** Sealed class representing the possible goal types a user can set: total daily time, per-app limit, or unlock limit. */
sealed class GoalType(val key: String, val displayName: String) {
    object TotalDaily : GoalType("TOTAL_DAILY", "Tiempo Total Diario")
    object AppLimit : GoalType("APP_LIMIT", "Límite de Aplicación")
    object UnlockLimit : GoalType("UNLOCK_LIMIT", "Límite Desbloqueos")

    companion object {
        /** Resolves a [GoalType] from its string [key]; defaults to [TotalDaily] for unknown keys. */
        fun fromKey(key: String): GoalType = when (key) {
            "TOTAL_DAILY" -> TotalDaily
            "APP_LIMIT" -> AppLimit
            "UNLOCK_LIMIT" -> UnlockLimit
            else -> TotalDaily
        }
    }
}
